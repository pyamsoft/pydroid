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
 */

package com.pyamsoft.pydroid.util;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.MessageQueue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import timber.log.Timber;

/**
 * Hopefully fixes Android's glorious InputMethodManager related context leaks.
 */
public final class IMMLeakUtil {

  /**
   * Fix for https://code.google.com/p/android/issues/detail?id=171190 .
   *
   * When a view that has focus gets detached, we wait for the main thread to be idle and then
   * check if the InputMethodManager is leaking a view. If yes, we tell it that the decor view got
   * focus, which is what happens if you press home and come back from recent apps. This replaces
   * the reference to the detached view with a reference to the decor view.
   *
   * Should be called from {@link Activity#onCreate(Bundle)} )}.
   */
  public static void fixFocusedViewLeak(Application application) {

    // LeakCanary reports this bug within IC_MR1 and M
    final int sdk = Build.VERSION.SDK_INT;
    if (sdk < Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1 || sdk > Build.VERSION_CODES.M) {
      Timber.w("Invalid version: %d", sdk);
      return;
    }

    final InputMethodManager inputMethodManager =
        (InputMethodManager) application.getSystemService(Context.INPUT_METHOD_SERVICE);

    final Field mServedViewField;
    final Field mHField;
    final Method finishInputLockedMethod;
    final Method focusInMethod;
    try {
      mServedViewField = InputMethodManager.class.getDeclaredField("mServedView");
      mHField = InputMethodManager.class.getDeclaredField("mServedView");
      finishInputLockedMethod = InputMethodManager.class.getDeclaredMethod("finishInputLocked");
      focusInMethod = InputMethodManager.class.getDeclaredMethod("focusIn", View.class);
      mServedViewField.setAccessible(true);
      mHField.setAccessible(true);
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
      @Override public void onActivityStarted(final Activity activity) {
        final ReferenceCleaner cleaner =
            new ReferenceCleaner(inputMethodManager, mHField, mServedViewField,
                finishInputLockedMethod);
        if (activity == null) {
          Timber.e("Activity is NULL");
          return;
        }
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
   * Simple class which allows us to not have to override every single callback, every single time.
   */
  public static class LifecycleCallbacksAdapter implements Application.ActivityLifecycleCallbacks {
    @Override public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override public void onActivityStarted(Activity activity) {

    }

    @Override public void onActivityResumed(Activity activity) {

    }

    @Override public void onActivityPaused(Activity activity) {

    }

    @Override public void onActivityStopped(Activity activity) {

    }

    @Override public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override public void onActivityDestroyed(Activity activity) {

    }
  }

  static class ReferenceCleaner
      implements MessageQueue.IdleHandler, View.OnAttachStateChangeListener,
      ViewTreeObserver.OnGlobalFocusChangeListener {

    private final InputMethodManager inputMethodManager;
    private final Field mHField;
    private final Field mServedViewField;
    private final Method finishInputLockedMethod;

    ReferenceCleaner(InputMethodManager inputMethodManager, Field mHField, Field mServedViewField,
        Method finishInputLockedMethod) {
      this.inputMethodManager = inputMethodManager;
      this.mHField = mHField;
      this.mServedViewField = mServedViewField;
      this.finishInputLockedMethod = finishInputLockedMethod;
    }

    @Override public void onGlobalFocusChanged(View oldFocus, View newFocus) {
      if (newFocus == null) {
        return;
      }
      if (oldFocus != null) {
        oldFocus.removeOnAttachStateChangeListener(this);
      }
      Looper.myQueue().removeIdleHandler(this);
      newFocus.addOnAttachStateChangeListener(this);
    }

    @Override public void onViewAttachedToWindow(View v) {
    }

    @Override public void onViewDetachedFromWindow(View v) {
      v.removeOnAttachStateChangeListener(this);
      Looper.myQueue().removeIdleHandler(this);
      Looper.myQueue().addIdleHandler(this);
    }

    @Override public boolean queueIdle() {
      clearInputMethodManagerLeak();
      return false;
    }

    @SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
    private void clearInputMethodManagerLeak() {
      try {
        Timber.d("Attempt to clear IMM leak");
        final Object lock = mHField.get(inputMethodManager);
        // This is highly dependent on the InputMethodManager implementation.
        synchronized (lock) {
          final View servedView = (View) mServedViewField.get(inputMethodManager);
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

    private Activity extractActivity(Context context) {
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
