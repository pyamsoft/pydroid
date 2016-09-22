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

class AboutLicenseItem {

  @NonNull private final String name;
  @NonNull private final String homepageUrl;
  @NonNull private final String licenseLocation;

  AboutLicenseItem(@NonNull String name, @NonNull String homepageUrl,
      @NonNull String licenseLocation) {
    this.name = name;
    this.homepageUrl = homepageUrl;
    this.licenseLocation = licenseLocation;
  }

  @NonNull @CheckResult String getName() {
    return name;
  }

  @NonNull @CheckResult String getHomepageUrl() {
    return homepageUrl;
  }

  @NonNull @CheckResult String getLicenseLocation() {
    return licenseLocation;
  }

  //@NonNull @CheckResult public static AboutAdapterItem licenseForAndroid() {
  //  return new AboutAdapterItem("Android", "https://source.android.com", Licenses.androidItem());
  //}
  //
  //@NonNull @CheckResult public static AboutAdapterItem licenseForAndroidSupport() {
  //  return new AboutAdapterItem("Android Support Libraries", "https://source.android.com",
  //      Licenses.androidSupportItem());
  //}
  //
  //@NonNull @CheckResult public static AboutAdapterItem licenseForPYDroid() {
  //  return new AboutAdapterItem("PYDroid", "https://pyamsoft.github.io/pydroid",
  //      Licenses.pydroidItem());
  //}
  //
  //@NonNull @CheckResult public static AboutAdapterItem licenseForAutoValue() {
  //  return new AboutAdapterItem("AutoValue", "https://pyamsoft.github.io/pydroid",
  //      Licenses.autovalueItem());
  //}
  //
  //@NonNull @CheckResult public static AboutAdapterItem licenseForRetrofit2() {
  //  return new AboutAdapterItem("Retrofit", "https://square.github.io/retrofit/",
  //      Licenses.retrofitItem());
  //}
  //
  //@NonNull @CheckResult public static AboutAdapterItem licenseForFirebase() {
  //  return new AboutAdapterItem("Firebase", "https://firebase.google.com/",
  //      Licenses.firebaseItem());
  //}
  //
  //@NonNull @CheckResult public static AboutAdapterItem licenseForDagger2() {
  //  return new AboutAdapterItem("Dagger", "https://google.github.io/dagger/",
  //      Licenses.daggerItem());
  //}
  //
  //@NonNull @CheckResult public static AboutAdapterItem licenseForAndroidCheckout() {
  //  return new AboutAdapterItem("Android Checkout", "https://github.com/serso/android-checkout",
  //      Licenses.androidCheckoutItem());
  //}
  //
  //@NonNull @CheckResult public static AboutAdapterItem licenseForButterknife() {
  //  return new AboutAdapterItem("Butterknife", "https://jakewharton.github.io/butterknife/",
  //      Licenses.butterknifeItem());
  //}
  //
  //@NonNull @CheckResult public static AboutAdapterItem licenseForLeakCanary() {
  //  return new AboutAdapterItem("Leak Canary", "https://github.com/square/leakcanary",
  //      Licenses.leakcanaryItem());
  //}
  //
  //@NonNull @CheckResult public static AboutAdapterItem licenseForRxJava() {
  //  return new AboutAdapterItem("RxJava", "https://github.com/ReactiveX/RxJava",
  //      Licenses.rxjavaItem());
  //}
  //
  //@NonNull @CheckResult public static AboutAdapterItem licenseForRxAndroid() {
  //  return new AboutAdapterItem("RxAndroid", "https://github.com/ReactiveX/RxAndroid",
  //      Licenses.rxandroidItem());
  //}
  //
  //@NonNull @CheckResult public static AboutAdapterItem licenseForFastAdapter() {
  //  return new AboutAdapterItem("FastAdapter", "https://github.com/mikepenz/FastAdapter",
  //      Licenses.fastAdapterItem());
  //}
  //
  //@NonNull @CheckResult public static AboutAdapterItem licenseForGooglePlayServices() {
  //  return new AboutAdapterItem("Google Play Services",
  //      "https://developers.google.com/android/guides/overview", Licenses.googlePlayItem());
  //}
  //
  //@NonNull @CheckResult public static AboutAdapterItem licenseForSQLBrite() {
  //  return new AboutAdapterItem("SQLBrite", "https://github.com/square/sqlbrite",
  //      Licenses.sqlbriteItem());
  //}
  //
  //@NonNull @CheckResult public static AboutAdapterItem licenseForSQLDelight() {
  //  return new AboutAdapterItem("SQLDelight", "https://github.com/square/sqldelight",
  //      Licenses.sqldelightItem());
  //}
  //
  //@NonNull @CheckResult public static AboutAdapterItem licenseForAndroidPriorityJobQueue() {
  //  return new AboutAdapterItem("Android Priority JobQueue",
  //      "https://github.com/yigit/android-priority-jobqueue",
  //      Licenses.androidPriorityJobQueueItem());
  //}
}
