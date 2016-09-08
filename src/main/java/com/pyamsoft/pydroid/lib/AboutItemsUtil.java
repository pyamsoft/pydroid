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

package com.pyamsoft.pydroid.lib;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.pyamsoft.pydroid.model.Licenses;

final class AboutItemsUtil {

  private AboutItemsUtil() {
    throw new RuntimeException("No instances");
  }

  @NonNull @CheckResult public static AboutItem licenseForAndroid() {
    return new AboutItem("Android", "https://source.android.com", Licenses.ANDROID);
  }

  @NonNull @CheckResult public static AboutItem licenseForAndroidSupport() {
    return new AboutItem("Android Support Libraries", "https://source.android.com",
        Licenses.ANDROID_SUPPORT);
  }

  @NonNull @CheckResult public static AboutItem licenseForPYDroid() {
    return new AboutItem("PYDroid", "https://pyamsoft.github.io/pydroid", Licenses.PYDROID);
  }

  @NonNull @CheckResult public static AboutItem licenseForAutoValue() {
    return new AboutItem("AutoValue", "https://pyamsoft.github.io/pydroid", Licenses.AUTO_VALUE);
  }

  @NonNull @CheckResult public static AboutItem licenseForRetrofit2() {
    return new AboutItem("Retrofit", "https://square.github.io/retrofit/", Licenses.RETROFIT2);
  }

  @NonNull @CheckResult public static AboutItem licenseForFirebase() {
    return new AboutItem("Firebase", "https://firebase.google.com/", Licenses.FIREBASE);
  }

  @NonNull @CheckResult public static AboutItem licenseForDagger2() {
    return new AboutItem("Dagger", "https://google.github.io/dagger/", Licenses.DAGGER);
  }

  @NonNull @CheckResult public static AboutItem licenseForAndroidInAppBilling() {
    return new AboutItem("Android In-App Billing v3",
        "https://github.com/anjlab/android-inapp-billing-v3", Licenses.ANDROID_IN_APP_BILLING);
  }

  @NonNull @CheckResult public static AboutItem licenseForButterknife() {
    return new AboutItem("Butterknife", "https://jakewharton.github.io/butterknife/",
        Licenses.BUTTERKNIFE);
  }

  @NonNull @CheckResult public static AboutItem licenseForLeakCanary() {
    return new AboutItem("Leak Canary", "https://github.com/square/leakcanary",
        Licenses.LEAK_CANARY);
  }

  @NonNull @CheckResult public static AboutItem licenseForRxJava() {
    return new AboutItem("RxJava", "https://github.com/ReactiveX/RxJava", Licenses.RXJAVA);
  }

  @NonNull @CheckResult public static AboutItem licenseForRxAndroid() {
    return new AboutItem("RxAndroid", "https://github.com/ReactiveX/RxAndroid", Licenses.RXANDROID);
  }

  @NonNull @CheckResult public static AboutItem licenseForFastAdapter() {
    return new AboutItem("FastAdapter", "https://github.com/mikepenz/FastAdapter",
        Licenses.FAST_ADAPTER);
  }

  @NonNull @CheckResult public static AboutItem licenseForGooglePlayServices() {
    return new AboutItem("Google Play Services",
        "https://developers.google.com/android/guides/overview", Licenses.GOOGLE_PLAY_SERVICES);
  }

  @NonNull @CheckResult public static AboutItem licenseForSQLBrite() {
    return new AboutItem("SQLBrite", "https://github.com/square/sqlbrite", Licenses.SQLBRITE);
  }

  @NonNull @CheckResult public static AboutItem licenseForSQLDelight() {
    return new AboutItem("SQLDelight", "https://github.com/square/sqldelight", Licenses.SQLDELIGHT);
  }

  @NonNull @CheckResult public static AboutItem licenseForAndroidPriorityJobQueue() {
    return new AboutItem("Android Priority JobQueue",
        "https://github.com/yigit/android-priority-jobqueue", Licenses.ANDROID_PRIORITY_JOBQUEUE);
  }
}
