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

package com.pyamsoft.pydroid.about;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

public final class AboutItemsUtil {

  private AboutItemsUtil() {
    throw new RuntimeException("No instances");
  }

  @NonNull @CheckResult public static AboutItem licenseForAndroid() {
    return new AboutItem("Android", "https://source.android.com", Licenses.androidItem());
  }

  @NonNull @CheckResult public static AboutItem licenseForAndroidSupport() {
    return new AboutItem("Android Support Libraries", "https://source.android.com",
        Licenses.androidSupportItem());
  }

  @NonNull @CheckResult public static AboutItem licenseForPYDroid() {
    return new AboutItem("PYDroid", "https://pyamsoft.github.io/pydroid", Licenses.pydroidItem());
  }

  @NonNull @CheckResult public static AboutItem licenseForAutoValue() {
    return new AboutItem("AutoValue", "https://pyamsoft.github.io/pydroid",
        Licenses.autovalueItem());
  }

  @NonNull @CheckResult public static AboutItem licenseForRetrofit2() {
    return new AboutItem("Retrofit", "https://square.github.io/retrofit/", Licenses.retrofitItem());
  }

  @NonNull @CheckResult public static AboutItem licenseForFirebase() {
    return new AboutItem("Firebase", "https://firebase.google.com/", Licenses.firebaseItem());
  }

  @NonNull @CheckResult public static AboutItem licenseForDagger2() {
    return new AboutItem("Dagger", "https://google.github.io/dagger/", Licenses.daggerItem());
  }

  @NonNull @CheckResult public static AboutItem licenseForAndroidCheckout() {
    return new AboutItem("Android Checkout", "https://github.com/serso/android-checkout",
        Licenses.androidCheckoutItem());
  }

  @NonNull @CheckResult public static AboutItem licenseForButterknife() {
    return new AboutItem("Butterknife", "https://jakewharton.github.io/butterknife/",
        Licenses.butterknifeItem());
  }

  @NonNull @CheckResult public static AboutItem licenseForLeakCanary() {
    return new AboutItem("Leak Canary", "https://github.com/square/leakcanary",
        Licenses.leakcanaryItem());
  }

  @NonNull @CheckResult public static AboutItem licenseForRxJava() {
    return new AboutItem("RxJava", "https://github.com/ReactiveX/RxJava", Licenses.rxjavaItem());
  }

  @NonNull @CheckResult public static AboutItem licenseForRxAndroid() {
    return new AboutItem("RxAndroid", "https://github.com/ReactiveX/RxAndroid",
        Licenses.rxandroidItem());
  }

  @NonNull @CheckResult public static AboutItem licenseForFastAdapter() {
    return new AboutItem("FastAdapter", "https://github.com/mikepenz/FastAdapter",
        Licenses.fastAdapterItem());
  }

  @NonNull @CheckResult public static AboutItem licenseForGooglePlayServices() {
    return new AboutItem("Google Play Services",
        "https://developers.google.com/android/guides/overview", Licenses.googlePlayItem());
  }

  @NonNull @CheckResult public static AboutItem licenseForSQLBrite() {
    return new AboutItem("SQLBrite", "https://github.com/square/sqlbrite", Licenses.sqlbriteItem());
  }

  @NonNull @CheckResult public static AboutItem licenseForSQLDelight() {
    return new AboutItem("SQLDelight", "https://github.com/square/sqldelight",
        Licenses.sqldelightItem());
  }

  @NonNull @CheckResult public static AboutItem licenseForAndroidPriorityJobQueue() {
    return new AboutItem("Android Priority JobQueue",
        "https://github.com/yigit/android-priority-jobqueue",
        Licenses.androidPriorityJobQueueItem());
  }
}
