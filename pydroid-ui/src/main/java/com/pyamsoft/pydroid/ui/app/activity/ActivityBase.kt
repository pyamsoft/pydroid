/*
 *     Copyright (C) 2017 Peter Kenji Yamanaka
 *
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.pyamsoft.pydroid.ui.app.activity

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.os.MessageQueue
import android.support.annotation.CallSuper
import android.support.annotation.CheckResult
import android.support.v7.app.AppCompatActivity
import android.support.v7.preference.PreferenceManager
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.app.fragment.BackPressHandler
import timber.log.Timber
import java.lang.reflect.Field
import java.lang.reflect.Method

abstract class ActivityBase : AppCompatActivity(), ToolbarActivity {

    /**
     * Override if you do not want to handle IMM leaks
     */
    protected open val shouldHandleIMMLeaks: Boolean = true
    private var capturedToolbar: Toolbar? = null

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        // These must go before the call to onCreate
        if (shouldHandleIMMLeaks) {
            IMMLeakUtil.fixFocusedViewLeak(application)
        }

        super.onCreate(savedInstanceState)
        PreferenceManager.setDefaultValues(this, R.xml.pydroid, false)
    }

    @CallSuper
    override fun onBackPressed() {
        supportFragmentManager.fragments.asSequence()
            .filter { it is BackPressHandler && it.onBackPressed() }.forEach { return }
        super.onBackPressed()
    }

    @CallSuper
    override fun onDestroy() {
        super.onDestroy()

        // Clear captured Toolbar
        capturedToolbar = null
    }

    override fun withToolbar(func: (Toolbar) -> Unit) {
        capturedToolbar?.let(func)
    }

    protected fun setToolbar(toolbar: Toolbar?) {
        capturedToolbar = toolbar
    }

    /**
     * Hopefully fixes Android's glorious InputMethodManager related context leaks.
     */
    private object IMMLeakUtil {

        /**
         * Simple class which allows us to not have to override every single callback, every single
         * time.
         */
        private open class LifecycleCallbacksAdapter : Application.ActivityLifecycleCallbacks {

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            }

            override fun onActivityStarted(activity: Activity) {
            }

            override fun onActivityResumed(activity: Activity) {
            }

            override fun onActivityPaused(activity: Activity) {
            }

            override fun onActivityStopped(activity: Activity) {
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
            }

            override fun onActivityDestroyed(activity: Activity) {
            }
        }

        private class ReferenceCleaner(
            private val inputMethodManager: InputMethodManager,
            private val lockField: Field, private val servedViewField: Field,
            private val finishInputLockedMethod: Method
        ) : MessageQueue.IdleHandler,
            View.OnAttachStateChangeListener, ViewTreeObserver.OnGlobalFocusChangeListener {

            override fun onGlobalFocusChanged(oldFocus: View?, newFocus: View?) {
                if (newFocus == null) {
                    return
                }
                oldFocus?.removeOnAttachStateChangeListener(this)
                Looper.myQueue().removeIdleHandler(this)
                newFocus.addOnAttachStateChangeListener(this)
            }

            override fun onViewAttachedToWindow(v: View) {}

            override fun onViewDetachedFromWindow(v: View) {
                v.removeOnAttachStateChangeListener(this)
                Looper.myQueue().removeIdleHandler(this)
                Looper.myQueue().addIdleHandler(this)
            }

            @CheckResult
            override fun queueIdle(): Boolean {
                clearInputMethodManagerLeak()
                return false
            }

            fun clearInputMethodManagerLeak() {
                try {
                    Timber.d("Attempt to clear IMM leak")
                    val lock = lockField.get(inputMethodManager)
                    if (lock == null) {
                        Timber.e("Null referenced used for lock")
                        return
                    }

                    // This is highly dependent on the InputMethodManager implementation.
                    synchronized(lock) {
                        val servedView = servedViewField.get(inputMethodManager) as View?
                        if (servedView != null) {

                            val servedViewAttached = servedView.windowVisibility != View.GONE

                            if (servedViewAttached) {
                                // The view held by the IMM was replaced without a global focus change. Let's make
                                // sure we with notified when that view detaches.

                                // Avoid double registration.
                                servedView.removeOnAttachStateChangeListener(this)
                                servedView.addOnAttachStateChangeListener(this)
                            } else {
                                // servedView is not attached. InputMethodManager is being stupid!
                                val activity = extractActivity(servedView.context)
                                if (activity == null || activity.window == null) {
                                    // Unlikely case. Let's finish the input anyways.
                                    Timber.d("Invoke finishInputLockedMethod")
                                    finishInputLockedMethod.invoke(inputMethodManager)
                                } else {
                                    val decorView = activity.window.peekDecorView()
                                    val windowAttached = decorView.windowVisibility != View.GONE
                                    if (!windowAttached) {
                                        Timber.d("Invoke finishInputLockedMethod")
                                        finishInputLockedMethod.invoke(inputMethodManager)
                                    } else {
                                        decorView.requestFocusFromTouch()
                                    }
                                }
                            }
                        }
                    }
                } catch (unexpected: Exception) {
                    Timber.e(unexpected, "Unexpected reflection exception")
                }
            }

            @CheckResult
            fun extractActivity(c: Context): Activity? {
                var context = c
                Timber.d("Extract the current activity from context")
                while (true) {
                    when (context) {
                        is Application -> return null
                        is Activity -> return context
                        is ContextWrapper -> {
                            val baseContext = context.baseContext
                            // Prevent Stack Overflow.
                            if (baseContext === context) {
                                return null
                            }
                            context = baseContext
                        }
                        else -> return null
                    }
                }
            }
        }

        /**
         * Fix for https://code.google.com/p/android/issues/detail?id=171190 .
         *
         * When a view that has focus gets detached, we wait for the obs thread to be idle and then
         * check if the InputMethodManager is leaking a view. If yes, we tell it that the decor view
         * got
         * focus, which is what happens if you press home and come back from recent apps. This replaces
         * the reference to the detached view with a reference to the decor view.
         *
         * Should be called from [Activity.onCreate] )}.
         */
        internal fun fixFocusedViewLeak(application: Application) {
            // LeakCanary reports this bug within IC_MR1 and M
            val sdk = Build.VERSION.SDK_INT
            if (sdk < Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1 || sdk > Build.VERSION_CODES.M) {
                Timber.w("Invalid version: %d", sdk)
                return
            }

            val inputMethodManager = application.getSystemService(
                Context.INPUT_METHOD_SERVICE
            ) as InputMethodManager

            val servedViewField: Field
            val lockField: Field
            val finishInputLockedMethod: Method
            val focusInMethod: Method
            try {
                servedViewField = InputMethodManager::class.java.getDeclaredField("mServedView")
                lockField = InputMethodManager::class.java.getDeclaredField("mServedView")
                finishInputLockedMethod = InputMethodManager::class.java.getDeclaredMethod(
                    "finishInputLocked"
                )
                focusInMethod = InputMethodManager::class.java.getDeclaredMethod(
                    "focusIn",
                    View::class.java
                )
                servedViewField.isAccessible = true
                lockField.isAccessible = true
                finishInputLockedMethod.isAccessible = true
                focusInMethod.isAccessible = true
            } catch (unexpected: Exception) {
                Timber.e(unexpected, "Unexpected reflection exception")
                return
            }

            // Change this based on when you wish to attach the callback
            // reports from gist state that onActivityStarted may be safer
            // https://gist.github.com/pyricau/4df64341cc978a7de414
            Timber.d("Register lifecycle callback to catch IMM Leaks")
            application.registerActivityLifecycleCallbacks(object : LifecycleCallbacksAdapter() {
                override fun onActivityStarted(activity: Activity) {
                    activity.window?.decorView?.rootView?.viewTreeObserver?.addOnGlobalFocusChangeListener(
                        ReferenceCleaner(
                            inputMethodManager, lockField, servedViewField,
                            finishInputLockedMethod
                        )
                    )
                }
            })
        }
    }
}
