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

package com.pyamsoft.pydroid.crash;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import java.io.File;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import timber.log.Timber;

public final class CrashHandler implements Thread.UncaughtExceptionHandler {

  @NonNull private static final String CRASH_FILE_PREFIX = "CRASH_";
  @NonNull private static final String CRASH_FILE_EXT = ".txt";

  @NonNull private final Context registeredContext;
  @NonNull private final CrashHandler.Provider provider;
  @NonNull private final Thread.UncaughtExceptionHandler defaultUncaughtExceptionHandler;
  @NonNull private final Handler mainHandler;

  private boolean crashing;
  private boolean unregistered;

  public CrashHandler(final @NonNull Context context,
      final @NonNull CrashHandler.Provider provider) {
    this.provider = provider;
    this.registeredContext = context.getApplicationContext();
    this.defaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
    this.mainHandler = new Handler(Looper.getMainLooper());

    crashing = false;
    unregistered = true;
  }

  @SuppressLint("NewApi") private String createCrashLog(final @NonNull Context context,
      final @NonNull Throwable throwable) {
    final String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
    final File dir = context.getApplicationContext().getFilesDir();
    if (dir == null) {
      Timber.e("No internal files dir!");
      return null;
    }

    final File crashFilesDir = new File(dir, "crashes");
    if (!crashFilesDir.exists()) {
      if (!crashFilesDir.mkdirs()) {
        Timber.e("Could not create crash file directories");
        return null;
      }
    }

    final String prefix = CRASH_FILE_PREFIX;
    final String crashFileName = prefix + timeStamp + CRASH_FILE_EXT;

    // Clear out old logs before making new ones
    clearOldCrashLogs(crashFilesDir);

    final File crashFile = new File(crashFilesDir, crashFileName);
    try
        (final PrintWriter printWriter = new PrintWriter(crashFile)) {
      populateCrashLog(context, printWriter, throwable);
      return crashFile.getAbsolutePath();
    } catch (Exception e) {
      Timber.e(e, "ERROR while trying to write log to printStream");
      return null;
    }
  }

  private void clearOldCrashLogs(@NonNull final File dir) {
    final File[] crashFiles = dir.listFiles();
    for (final File crash : crashFiles) {
      if (crash.getName().startsWith(CRASH_FILE_PREFIX)) {
        Timber.w("Removing old crash file: %s", crash);
        if (crash.delete()) {
          Timber.i("Old crash file: %s has been deleted", crash);
        }
      }
    }
  }

  private void populateCrashLog(final @NonNull Context context, @NonNull PrintWriter printWriter,
      final Throwable throwable) {
    printWriter.println("PACKAGE: " + context.getApplicationContext().getPackageName());
    printWriter.println();
    printWriter.println("APPLICATION ID: " + provider.buildConfigApplicationId());
    printWriter.println("VERSION NAME: " + provider.buildConfigVersionName());
    printWriter.println("VERSION CODE: " + provider.buildConfigVersionCode());
    printWriter.println();
    printWriter.println();
    printWriter.println("ANDROID VERSION: " + Build.VERSION.SDK_INT);
    printWriter.println("ANDROID RELEASE: " + Build.VERSION.RELEASE);
    printWriter.println("ANDROID CODENAME: " + Build.VERSION.CODENAME);
    printWriter.println();
    printWriter.println();
    printWriter.println("DEVICE MODEL: " + Build.MODEL);
    printWriter.println("DEVICE HARDWARE: " + Build.HARDWARE);
    printWriter.println("DEVICE MANUFACTURER: " + Build.MANUFACTURER);
    printWriter.println("DEVICE BRAND: " + Build.BRAND);
    printWriter.println("DEVICE PRODUCT: " + Build.PRODUCT);
    printWriter.println();
    printWriter.println();
    printWriter.println("STACK TRACE FOLLOWS");
    printWriter.println();
    throwable.printStackTrace(printWriter);
  }

  private boolean startCrashLogActivity(final @NonNull Context context,
      final @NonNull Throwable throwable) {
    try {
      final String crashLogPath = createCrashLog(context, throwable);
      if (crashLogPath == null) {
        Timber.e("Could not create crash Log file");
        return false;
      }

      Timber.e("Start Crash Log Intent");
      final Intent crashLogIntent = new Intent(provider.getPackageName() + ".crash.SEND_LOG");
      crashLogIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      crashLogIntent.putExtra(CrashLogActivity.CRASH_SUBJECT, provider.crashLogSubject())
          .putExtra(CrashLogActivity.CRASH_TEXT, provider.crashLogText())
          .putExtra(CrashLogActivity.CRASH_FILE, crashLogPath)
          .putExtra(CrashLogActivity.APP_NAME, provider.appName());
      context.getApplicationContext().startActivity(crashLogIntent);
      return true;
    } catch (Exception e) {
      Timber.e(e, "Error attempting to start crash activity!");
      return false;
    }
  }

  private void invokeCrashLogActivity(final Thread thread, final Throwable throwable) {
    try {
      if (startCrashLogActivity(registeredContext, throwable)) {
        return;
      }
      continueWithOriginalCrash(thread, throwable);
    } finally {
      terminateProcess();
    }
  }

  private void terminateProcess() {
    final int pid = android.os.Process.myPid();
    Timber.w("Terminating Process: %d", pid);
    android.os.Process.killProcess(pid);
  }

  private void continueWithOriginalCrash(final @NonNull Thread thread,
      final @NonNull Throwable throwable) {
    // Pass through the original exception
    Timber.e("Pass original throwable");
    defaultUncaughtExceptionHandler.uncaughtException(thread, throwable);
  }

  @Override public void uncaughtException(@NonNull Thread thread, @NonNull Throwable throwable) {
    // Avoids a potential stack overflow
    if (crashing) {
      return;
    }

    if (unregistered) {
      Timber.e("This CrashHandler is not registered");
      return;
    }

    crashing = true;
    Timber.e(throwable, "UNCAUGHT EXCEPTION OCCURRED");

    // If the exception is thrown from the UI thread
    if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
      invokeCrashLogActivity(thread, throwable);
    } else {
      mainHandler.post(() -> invokeCrashLogActivity(thread, throwable));
    }
  }

  public final void unregister() {
    if (!unregistered) {
      if (Thread.getDefaultUncaughtExceptionHandler() == this) {
        Thread.setDefaultUncaughtExceptionHandler(defaultUncaughtExceptionHandler);
      }
      unregistered = true;
    }
  }

  public final void register() {
    if (unregistered) {
      Thread.setDefaultUncaughtExceptionHandler(this);
      unregistered = false;
    }
  }

  public interface Provider {

    @CheckResult @NonNull String appName();

    @CheckResult @NonNull String buildConfigApplicationId();

    @CheckResult @NonNull String buildConfigVersionName();

    @CheckResult int buildConfigVersionCode();

    @CheckResult @NonNull String getPackageName();

    @CheckResult String crashLogText();

    @CheckResult String crashLogSubject();
  }
}
