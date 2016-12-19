/*
 * Copyright 2016 Peter Kenji Yamanaka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.pyamsoft.pydroid.app.activity;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.MessageQueue;
import android.support.annotation.CallSuper;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.preference.PreferenceManager;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import com.pyamsoft.pydroid.R;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import timber.log.Timber;

public abstract class ActivityBase extends AppCompatActivity {

  static {
    // Attempt to fix issues with Vectors on API 19
    AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
  }

  /**
   * Override if you do not want to handle IMM leaks
   */
  @SuppressWarnings({ "WeakerAccess", "SameReturnValue" }) @CheckResult
  protected boolean shouldHandleIMMLeaks() {
    return true;
  }

  /**
   * Override if you do not want the Window to behave like a fullscreen one
   */
  @SuppressWarnings({ "SameReturnValue", "WeakerAccess" }) @CheckResult
  protected boolean isFakeFullscreen() {
    return false;
  }

  @SuppressWarnings("WeakerAccess") void setupFakeFullscreenWindow() {
    getWindow().getDecorView()
        .setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
  }

  @CallSuper @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    // These must go before the call to onCreate
    if (shouldHandleIMMLeaks()) {
      IMMLeakUtil.fixFocusedViewLeak(getApplication());
    }
    if (isFakeFullscreen()) {
      setupFakeFullscreenWindow();
    }

    super.onCreate(savedInstanceState);
    PreferenceManager.setDefaultValues(this, R.xml.pydroid, false);
  }

  /**
   * Hopefully fixes Android's glorious InputMethodManager related context leaks.
   */
  static final class IMMLeakUtil {

    private IMMLeakUtil() {
      throw new RuntimeException("No instances");
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
     * Should be called from {@link Activity#onCreate(Bundle)} )}.
     */
    static void fixFocusedViewLeak(@NonNull Application application) {

      // LeakCanary reports this bug within IC_MR1 and M
      final int sdk = Build.VERSION.SDK_INT;
      if (sdk < Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1 || sdk > Build.VERSION_CODES.M) {
        Timber.w("Invalid version: %d", sdk);
        return;
      }

      final InputMethodManager inputMethodManager =
          (InputMethodManager) application.getSystemService(INPUT_METHOD_SERVICE);

      final Field servedViewField;
      final Field lockField;
      final Method finishInputLockedMethod;
      final Method focusInMethod;
      try {
        servedViewField = InputMethodManager.class.getDeclaredField("mServedView");
        lockField = InputMethodManager.class.getDeclaredField("mServedView");
        finishInputLockedMethod = InputMethodManager.class.getDeclaredMethod("finishInputLocked");
        focusInMethod = InputMethodManager.class.getDeclaredMethod("focusIn", View.class);
        servedViewField.setAccessible(true);
        lockField.setAccessible(true);
        finishInputLockedMethod.setAccessible(true);
        focusInMethod.setAccessible(true);
      } catch (final Exception unexpected) {
        Timber.e(unexpected, "Unexpected reflection exception");
        return;
      }

      // Change this based on when you wish to attach the callback
      // reports from gist state that onActivityStarted may be safer
      // https://gist.github.com/pyricau/4df64341cc978a7de414
      Timber.d("Register lifecycle callback to catch IMM Leaks");
      application.registerActivityLifecycleCallbacks(new LifecycleCallbacksAdapter() {
        @Override public void onActivityStarted(@NonNull final Activity activity) {
          final ReferenceCleaner cleaner =
              new ReferenceCleaner(inputMethodManager, lockField, servedViewField,
                  finishInputLockedMethod);
          final Window window = activity.getWindow();
          if (window == null) {
            Timber.e("Activity Window is NULL");
            return;
          }
          final View decorView = window.getDecorView();
          if (decorView == null) {
            Timber.e("Window DecorView is NULL");
            return;
          }
          final View rootView = decorView.getRootView();
          if (rootView == null) {
            Timber.e("DecorView Root is NULL");
            return;
          }

          final ViewTreeObserver viewTreeObserver = rootView.getViewTreeObserver();
          if (viewTreeObserver != null) {
            viewTreeObserver.addOnGlobalFocusChangeListener(cleaner);
          }
        }
      });
    }

    /**
     * Simple class which allows us to not have to override every single callback, every single
     * time.
     */
    static class LifecycleCallbacksAdapter implements Application.ActivityLifecycleCallbacks {

      @Override public void onActivityCreated(@NonNull Activity activity,
          @NonNull Bundle savedInstanceState) {

      }

      @Override public void onActivityStarted(@NonNull Activity activity) {

      }

      @Override public void onActivityResumed(@NonNull Activity activity) {

      }

      @Override public void onActivityPaused(@NonNull Activity activity) {

      }

      @Override public void onActivityStopped(@NonNull Activity activity) {

      }

      @Override public void onActivitySaveInstanceState(@NonNull Activity activity,
          @NonNull Bundle outState) {

      }

      @Override public void onActivityDestroyed(@NonNull Activity activity) {

      }
    }

    static class ReferenceCleaner
        implements MessageQueue.IdleHandler, View.OnAttachStateChangeListener,
        ViewTreeObserver.OnGlobalFocusChangeListener {

      @NonNull final InputMethodManager inputMethodManager;
      @NonNull final Field lockField;
      @NonNull final Field servedViewField;
      @NonNull final Method finishInputLockedMethod;

      ReferenceCleaner(@NonNull InputMethodManager inputMethodManager, @NonNull Field lockField,
          @NonNull Field servedViewField, @NonNull Method finishInputLockedMethod) {
        this.inputMethodManager = inputMethodManager;
        this.lockField = lockField;
        this.servedViewField = servedViewField;
        this.finishInputLockedMethod = finishInputLockedMethod;
      }

      @Override public void onGlobalFocusChanged(@Nullable View oldFocus, @Nullable View newFocus) {
        if (newFocus == null) {
          return;
        }
        if (oldFocus != null) {
          oldFocus.removeOnAttachStateChangeListener(this);
        }
        Looper.myQueue().removeIdleHandler(this);
        newFocus.addOnAttachStateChangeListener(this);
      }

      @Override public void onViewAttachedToWindow(@NonNull View v) {
      }

      @Override public void onViewDetachedFromWindow(@NonNull View v) {
        v.removeOnAttachStateChangeListener(this);
        Looper.myQueue().removeIdleHandler(this);
        Looper.myQueue().addIdleHandler(this);
      }

      @CheckResult @Override public boolean queueIdle() {
        clearInputMethodManagerLeak();
        return false;
      }

      @SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
      void clearInputMethodManagerLeak() {
        try {
          Timber.d("Attempt to clear IMM leak");
          final Object lock = lockField.get(inputMethodManager);
          if (lock == null) {
            Timber.e("Null referenced used for lock");
            return;
          }

          // This is highly dependent on the InputMethodManager implementation.
          synchronized (lock) {
            final View servedView = (View) servedViewField.get(inputMethodManager);
            if (servedView != null) {

              final boolean servedViewAttached = servedView.getWindowVisibility() != View.GONE;

              if (servedViewAttached) {
                // The view held by the IMM was replaced without a global focus change. Let's make
                // sure we get notified when that view detaches.

                // Avoid double registration.
                servedView.removeOnAttachStateChangeListener(this);
                servedView.addOnAttachStateChangeListener(this);
              } else {
                // servedView is not attached. InputMethodManager is being stupid!
                final Activity activity = extractActivity(servedView.getContext());
                if (activity == null || activity.getWindow() == null) {
                  // Unlikely case. Let's finish the input anyways.
                  Timber.d("Invoke finishInputLockedMethod");
                  finishInputLockedMethod.invoke(inputMethodManager);
                } else {
                  final View decorView = activity.getWindow().peekDecorView();
                  final boolean windowAttached = decorView.getWindowVisibility() != View.GONE;
                  if (!windowAttached) {
                    Timber.d("Invoke finishInputLockedMethod");
                    finishInputLockedMethod.invoke(inputMethodManager);
                  } else {
                    decorView.requestFocusFromTouch();
                  }
                }
              }
            }
          }
        } catch (final Exception unexpected) {
          Timber.e(unexpected, "Unexpected reflection exception");
        }
      }

      @CheckResult @Nullable Activity extractActivity(@NonNull Context context) {
        Timber.d("Extract the current activity from context");
        while (true) {
          if (context instanceof Application) {
            return null;
          } else if (context instanceof Activity) {
            return (Activity) context;
          } else if (context instanceof ContextWrapper) {
            final Context baseContext = ((ContextWrapper) context).getBaseContext();
            // Prevent Stack Overflow.
            if (baseContext == context) {
              return null;
            }
            context = baseContext;
          } else {
            return null;
          }
        }
      }
    }
  }
}

