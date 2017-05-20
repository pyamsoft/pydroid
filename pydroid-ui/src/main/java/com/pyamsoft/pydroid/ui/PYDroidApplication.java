/*
 * Copyright 2017 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.ui;

import android.app.Application;
import android.os.StrictMode;
import android.support.annotation.CheckResult;
import com.pyamsoft.pydroid.PYDroidModule;
import timber.log.Timber;

public abstract class PYDroidApplication extends Application {

  /**
   * The onCreate method expects a very strict application flow.
   *
   * We mark it as final here and then allow custom injection via hook methods
   */
  @Override public final void onCreate() {
    super.onCreate();
    if (exitBeforeInitialization()) {
      Timber.w("Exiting out before PYDroidApplication initialization");
      return;
    }

    insertLicensesIntoMap();

    final boolean debug = isDebugMode();
    onFirstCreate(debug);

    if (debug) {
      onDebugApplicationCreated();
    } else {
      onReleaseApplicationCreated();
    }
    onApplicationCreated();
  }

  /**
   * Add a list of known licenses used into the License map
   */
  private void insertLicensesIntoMap() {
    UiLicenses.addLicenses();
    insertCustomLicensesIntoMap();
  }

  /**
   * On the initial create, we setup the injector with PYDroid module
   *
   * We also run through debug mode setup of things like Timber and Strict mode
   */
  private void onFirstCreate(boolean debug) {
    PYDroidInjector.set(
        PYDroidComponent.withModule(new PYDroidModule(getApplicationContext(), debug)));
    if (debug) {
      Timber.plant(new Timber.DebugTree());
      setStrictMode();
    }
  }

  /**
   * Sets strict mode flags when running in debug mode
   */
  private void setStrictMode() {
    StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll()
        .penaltyLog()
        .penaltyDeath()
        .permitDiskReads()
        .permitDiskWrites()
        .penaltyFlashScreen()
        .build());
    StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build());
  }

  /**
   * Because the Application class can exist multiple times in a Multi process application,
   * we do not always want to initialize it. This allows us to early bail out
   * of a multi process initialization.
   */
  @CheckResult protected boolean exitBeforeInitialization() {
    return false;
  }

  /**
   * Hook for application events, meant to run in all cases
   */
  protected void onApplicationCreated() {

  }

  /**
   * Hook for application events for DEBUG builds
   */
  protected void onDebugApplicationCreated() {

  }

  /**
   * Hook for application events for RELEASE builds
   */
  protected void onReleaseApplicationCreated() {

  }

  /**
   * Is the application in DEBUG mode
   */
  @CheckResult protected abstract boolean isDebugMode();

  /**
   * Add any custom licenses used by the application
   */
  protected void insertCustomLicensesIntoMap() {

  }
}
