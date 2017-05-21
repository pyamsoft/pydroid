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

package com.pyamsoft.pydroid;

import android.content.Context;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import com.pyamsoft.pydroid.about.AboutLibrariesModel;
import com.pyamsoft.pydroid.about.Licenses;
import com.pyamsoft.pydroid.helper.Checker;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import java.util.Collections;
import java.util.List;

@RestrictTo(RestrictTo.Scope.LIBRARY) public class PYDroidModule {

  // Singleton
  @NonNull private final Context appContext;
  @NonNull private final List<AboutLibrariesModel> licenses;
  @NonNull private final PYDroidPreferencesImpl preferences;
  private final boolean debug;

  public PYDroidModule(@NonNull Context context, boolean debug) {
    this.debug = debug;
    appContext = Checker.checkNonNull(context).getApplicationContext();
    licenses = Licenses.getLicenses();
    preferences = new PYDroidPreferencesImpl(appContext);
  }

  @CheckResult public final boolean isDebug() {
    return debug;
  }

  // Singleton
  @CheckResult @NonNull public final Context provideContext() {
    return appContext;
  }

  // Singleton
  @CheckResult @NonNull public final RatingPreferences provideRatingPreferences() {
    return preferences;
  }

  // Singleton
  @CheckResult @NonNull public final List<AboutLibrariesModel> provideLicenseMap() {
    return Collections.unmodifiableList(licenses);
  }

  // Singleton
  @CheckResult @NonNull public final Scheduler provideSubScheduler() {
    return Schedulers.io();
  }

  // Singleton
  @CheckResult @NonNull public final Scheduler provideObsScheduler() {
    return AndroidSchedulers.mainThread();
  }
}
