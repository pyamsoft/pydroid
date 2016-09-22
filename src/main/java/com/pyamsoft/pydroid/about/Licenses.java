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

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import rx.functions.Action1;

public final class Licenses {

  @NonNull private static final Licenses INSTANCE = new Licenses();

  @NonNull private final Map<String, AboutLicenseItem> aboutItemMap;

  private Licenses() {
    aboutItemMap = new HashMap<>();
    addCommonLicenses();
  }

  public static void create(@NonNull String name, @NonNull String homepageUrl,
      @NonNull String licenseLocation) {
    INSTANCE.createItem(name, homepageUrl, licenseLocation);
  }

  static void forEach(@NonNull Action1<AboutLicenseItem> action) {
    INSTANCE.forEachItem(action);
  }

  /**
   * These libraries are directly used by PYDroid, and are thus in every application that uses
   * pydroid
   */
  private void addCommonLicenses() {
    createItem(Names.ANDROID, HomepageUrls.ANDROID, LicenseLocations.ANDROID);
    createItem(Names.ANDROID_SUPPORT, HomepageUrls.ANDROID_SUPPORT,
        LicenseLocations.ANDROID_SUPPORT);
    createItem(Names.GOOGLE_PLAY, HomepageUrls.GOOGLE_PLAY, LicenseLocations.GOOGLE_PLAY);
    createItem(Names.PYDROID, HomepageUrls.PYDROID, LicenseLocations.PYDROID);
    createItem(Names.AUTO_VALUE, HomepageUrls.AUTO_VALUE, LicenseLocations.AUTO_VALUE);
    createItem(Names.RETROFIT, HomepageUrls.RETROFIT, LicenseLocations.RETROFIT);
    createItem(Names.FIREBASE, HomepageUrls.FIREBASE, LicenseLocations.FIREBASE);
    createItem(Names.DAGGER2, HomepageUrls.DAGGER2, LicenseLocations.DAGGER2);
    createItem(Names.ANDROID_CHECKOUT, HomepageUrls.ANDROID_CHECKOUT,
        LicenseLocations.ANDROID_CHECKOUT);
    createItem(Names.BUTTERKNIFE, HomepageUrls.BUTTERKNIFE, LicenseLocations.BUTTERKNIFE);
    createItem(Names.LEAK_CANARY, HomepageUrls.LEAK_CANARY, LicenseLocations.LEAK_CANARY);
    createItem(Names.RXJAVA, HomepageUrls.RXJAVA, LicenseLocations.RXJAVA);
    createItem(Names.RXANDROID, HomepageUrls.RXANDROID, LicenseLocations.RXANDROID);
    createItem(Names.FAST_ADAPTER, HomepageUrls.FAST_ADAPTER, LicenseLocations.FAST_ADAPTER);
    createItem(Names.ERROR_PRONE, HomepageUrls.ERROR_PRONE, LicenseLocations.ERROR_PRONE);
    createItem(Names.TAP_TARGET_VIEW, HomepageUrls.TAP_TARGET_VIEW,
        LicenseLocations.TAP_TARGET_VIEW);
    createItem(Names.TIMBER, HomepageUrls.TIMBER, LicenseLocations.TIMBER);
  }

  @VisibleForTesting @SuppressWarnings("WeakerAccess") void createItem(@NonNull String name,
      @NonNull String homepageUrl, @NonNull String licenseLocation) {
    final AboutLicenseItem item = new AboutLicenseItem(name, homepageUrl, licenseLocation);
    aboutItemMap.put(name, item);
  }

  @VisibleForTesting @SuppressWarnings("WeakerAccess") void forEachItem(
      @NonNull Action1<AboutLicenseItem> action) {
    final List<AboutLicenseItem> sortedValues = new ArrayList<>(aboutItemMap.values());
    Collections.sort(sortedValues, (o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName()));
    for (final AboutLicenseItem item : sortedValues) {
      if (item != null) {
        action.call(item);
      }
    }
  }

  static final class Names {
    @NonNull static final String ANDROID = "Android";
    @NonNull static final String ANDROID_SUPPORT = "Android Support Libraries";
    @NonNull static final String GOOGLE_PLAY = "Google Play Services";
    @NonNull static final String PYDROID = "PYDroid";
    @NonNull static final String AUTO_VALUE = "AutoValue";
    @NonNull static final String RETROFIT = "Retrofit";
    @NonNull static final String FIREBASE = "Firebase";
    @NonNull static final String DAGGER2 = "Dagger2";
    @NonNull static final String ANDROID_CHECKOUT = "Android Checkout";
    @NonNull static final String BUTTERKNIFE = "Butterknife";
    @NonNull static final String LEAK_CANARY = "Leak Canary";
    @NonNull static final String RXJAVA = "RxJava";
    @NonNull static final String RXANDROID = "RxAndroid";
    @NonNull static final String FAST_ADAPTER = "Fast Adapter";
    @NonNull static final String ERROR_PRONE = "Error Prone";
    @NonNull static final String TAP_TARGET_VIEW = "TapTargetView";
    @NonNull static final String TIMBER = "Timber";

    private Names() {
      throw new RuntimeException("No instances");
    }
  }

  @SuppressWarnings("WeakerAccess") static final class HomepageUrls {
    @NonNull static final String ANDROID = "https://source.android.com";
    @NonNull static final String ANDROID_SUPPORT = "https://source.android.com";
    @NonNull static final String GOOGLE_PLAY =
        "https://developers.google.com/android/guides/overview";
    @NonNull static final String PYDROID = "https://pyamsoft.github.io/pydroid";
    @NonNull static final String AUTO_VALUE = "https://github.com/google/auto";
    @NonNull static final String RETROFIT = "https://square.github.io/retrofit/";
    @NonNull static final String FIREBASE = "https://firebase.google.com/";
    @NonNull static final String DAGGER2 = "https://google.github.io/dagger/";
    @NonNull static final String ANDROID_CHECKOUT = "https://github.com/serso/android-checkout";
    @NonNull static final String BUTTERKNIFE = "https://jakewharton.github.io/butterknife/";
    @NonNull static final String LEAK_CANARY = "https://github.com/square/leakcanary";
    @NonNull static final String RXJAVA = "https://github.com/ReactiveX/RxJava";
    @NonNull static final String RXANDROID = "https://github.com/ReactiveX/RxAndroid";
    @NonNull static final String FAST_ADAPTER = "https://github.com/mikepenz/FastAdapter";
    @NonNull static final String ERROR_PRONE = "https://github.com/google/error-prone";
    @NonNull static final String TAP_TARGET_VIEW = "https://github.com/KeepSafe/TapTargetView";
    @NonNull static final String TIMBER = "https://github.com/JakeWharton/timber";

    private HomepageUrls() {
      throw new RuntimeException("No instances");
    }
  }

  @SuppressWarnings("WeakerAccess") static final class LicenseLocations {
    // Add an underscore to keep this name on top
    @NonNull private static final String _BASE = "licenses/";
    @NonNull static final String ANDROID_SUPPORT = _BASE + "androidsupport";
    @NonNull static final String ANDROID = _BASE + "android";
    @NonNull static final String GOOGLE_PLAY = _BASE + "";
    @NonNull static final String PYDROID = _BASE + "pydroid";
    @NonNull static final String AUTO_VALUE = _BASE + "autovalue";
    @NonNull static final String RETROFIT = _BASE + "retrofit";
    @NonNull static final String FIREBASE = _BASE + "firebase";
    @NonNull static final String DAGGER2 = _BASE + "dagger2";
    @NonNull static final String ANDROID_CHECKOUT = _BASE + "androidcheckout";
    @NonNull static final String BUTTERKNIFE = _BASE + "butterknife";
    @NonNull static final String LEAK_CANARY = _BASE + "leakcanary";
    @NonNull static final String RXJAVA = _BASE + "rxjava";
    @NonNull static final String RXANDROID = _BASE + "rxandroid";
    @NonNull static final String FAST_ADAPTER = _BASE + "fastadapter";
    @NonNull static final String ERROR_PRONE = _BASE + "errorprone";
    @NonNull static final String TAP_TARGET_VIEW = _BASE + "taptargetview";
    @NonNull static final String TIMBER = _BASE + "timber";

    private LicenseLocations() {
      throw new RuntimeException("No instances");
    }
  }

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
