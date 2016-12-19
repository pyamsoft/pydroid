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

package com.pyamsoft.pydroid.about;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import com.pyamsoft.pydroid.ActionSingle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Licenses {

  @NonNull private static final Licenses INSTANCE = new Licenses();

  @NonNull private final Map<String, AboutLicenseModel> aboutItemMap;

  private Licenses() {
    aboutItemMap = new HashMap<>();
    addCommonLicenses();
  }

  public static void create(@NonNull String name, @NonNull String homepageUrl,
      @NonNull String licenseLocation) {
    INSTANCE.createItem(name, homepageUrl, licenseLocation);
  }

  public static void forEach(@NonNull ActionSingle<AboutLicenseModel> action) {
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
    createItem(Names.PYDROID, HomepageUrls.PYDROID, LicenseLocations.PYDROID);
    createItem(Names.AUTO_VALUE, HomepageUrls.AUTO_VALUE, LicenseLocations.AUTO_VALUE);
    createItem(Names.RETROFIT, HomepageUrls.RETROFIT, LicenseLocations.RETROFIT);
    createItem(Names.FIREBASE, HomepageUrls.FIREBASE, LicenseLocations.FIREBASE);
    createItem(Names.DAGGER2, HomepageUrls.DAGGER2, LicenseLocations.DAGGER2);
    createItem(Names.ANDROID_CHECKOUT, HomepageUrls.ANDROID_CHECKOUT,
        LicenseLocations.ANDROID_CHECKOUT);
    createItem(Names.LEAK_CANARY, HomepageUrls.LEAK_CANARY, LicenseLocations.LEAK_CANARY);
    createItem(Names.RXJAVA, HomepageUrls.RXJAVA, LicenseLocations.RXJAVA);
    createItem(Names.RXANDROID, HomepageUrls.RXANDROID, LicenseLocations.RXANDROID);
    createItem(Names.FAST_ADAPTER, HomepageUrls.FAST_ADAPTER, LicenseLocations.FAST_ADAPTER);
    createItem(Names.ERROR_PRONE, HomepageUrls.ERROR_PRONE, LicenseLocations.ERROR_PRONE);
    createItem(Names.TAP_TARGET_VIEW, HomepageUrls.TAP_TARGET_VIEW,
        LicenseLocations.TAP_TARGET_VIEW);
    createItem(Names.TIMBER, HomepageUrls.TIMBER, LicenseLocations.TIMBER);
    createItem(Names.RETROLAMBDA, HomepageUrls.RETROLAMBDA, LicenseLocations.RETROLAMBDA);
    createItem(Names.GRADLE_RETROLAMBDA, HomepageUrls.GRADLE_RETROLAMBDA,
        LicenseLocations.GRADLE_RETROLAMBDA);
    createItem(Names.GRADLE_VERSIONS_PLUGIN, HomepageUrls.GRADLE_VERSIONS_PLUGIN,
        LicenseLocations.GRADLE_VERSIONS_PLUGIN);
    createItem(Names.DEXCOUNT_GRADLE_PLUGIN, HomepageUrls.DEXCOUNT_GRADLE_PLUGIN,
        LicenseLocations.DEXCOUNT_GRADLE_PLUGIN);

    createItem(Names.GOOGLE_PLAY, HomepageUrls.GOOGLE_PLAY, LicenseLocations.GOOGLE_PLAY);
  }

  @VisibleForTesting @SuppressWarnings("WeakerAccess") void createItem(@NonNull String name,
      @NonNull String homepageUrl, @NonNull String licenseLocation) {
    final AboutLicenseModel item = AboutLicenseModel.create(name, homepageUrl, licenseLocation);
    aboutItemMap.put(name, item);
  }

  @VisibleForTesting @SuppressWarnings("WeakerAccess") void forEachItem(
      @NonNull ActionSingle<AboutLicenseModel> action) {
    final List<AboutLicenseModel> sortedValues = new ArrayList<>(aboutItemMap.values());
    Collections.sort(sortedValues, (o1, o2) -> o1.name().compareToIgnoreCase(o2.name()));
    //noinspection Convert2streamapi
    for (final AboutLicenseModel item : sortedValues) {
      if (item != null) {
        action.call(item);
      }
    }
  }

  public static final class Names {
    @NonNull public static final String ANDROID = "Android";
    @NonNull public static final String ANDROID_SUPPORT = "Android Support Libraries";
    @NonNull public static final String GOOGLE_PLAY = "Google Play Services";
    @NonNull public static final String PYDROID = "PYDroid";
    @NonNull public static final String AUTO_VALUE = "AutoValue";
    @NonNull public static final String RETROFIT = "Retrofit";
    @NonNull public static final String FIREBASE = "Firebase";
    @NonNull public static final String DAGGER2 = "Dagger2";
    @NonNull public static final String ANDROID_CHECKOUT = "Android Checkout";
    @NonNull public static final String LEAK_CANARY = "Leak Canary";
    @NonNull public static final String RXJAVA = "RxJava";
    @NonNull public static final String RXANDROID = "RxAndroid";
    @NonNull public static final String FAST_ADAPTER = "Fast Adapter";
    @NonNull public static final String ERROR_PRONE = "Error Prone";
    @NonNull public static final String TAP_TARGET_VIEW = "TapTargetView";
    @NonNull public static final String TIMBER = "Timber";
    @NonNull public static final String RETROLAMBDA = "Retrolambda";
    @NonNull public static final String GRADLE_RETROLAMBDA = "Gradle Retrolambda";
    @NonNull public static final String DEXCOUNT_GRADLE_PLUGIN = "Dexcount Gradle Plugin";
    @NonNull public static final String GRADLE_VERSIONS_PLUGIN = "Gradle Versions Plugin";

    private Names() {
      throw new RuntimeException("No instances");
    }
  }

  public static final class HomepageUrls {
    @NonNull public static final String ANDROID = "https://source.android.com";
    @NonNull public static final String ANDROID_SUPPORT = "https://source.android.com";
    @NonNull public static final String GOOGLE_PLAY =
        "https://developers.google.com/android/guides/overview";
    @NonNull public static final String PYDROID = "https://pyamsoft.github.io/pydroid";
    @NonNull public static final String AUTO_VALUE = "https://github.com/google/auto";
    @NonNull public static final String RETROFIT = "https://square.github.io/retrofit/";
    @NonNull public static final String FIREBASE = "https://firebase.google.com/";
    @NonNull public static final String DAGGER2 = "https://google.github.io/dagger/";
    @NonNull public static final String ANDROID_CHECKOUT =
        "https://github.com/serso/android-checkout";
    @NonNull public static final String LEAK_CANARY = "https://github.com/square/leakcanary";
    @NonNull public static final String RXJAVA = "https://github.com/ReactiveX/RxJava";
    @NonNull public static final String RXANDROID = "https://github.com/ReactiveX/RxAndroid";
    @NonNull public static final String FAST_ADAPTER = "https://github.com/mikepenz/FastAdapter";
    @NonNull public static final String ERROR_PRONE = "https://github.com/google/onError-prone";
    @NonNull public static final String TAP_TARGET_VIEW =
        "https://github.com/KeepSafe/TapTargetView";
    @NonNull public static final String TIMBER = "https://github.com/JakeWharton/timber";
    @NonNull public static final String RETROLAMBDA = "https://github.com/orfjackal/retrolambda";
    @NonNull public static final String GRADLE_RETROLAMBDA =
        "https://github.com/evant/gradle-retrolambda";
    @NonNull public static final String DEXCOUNT_GRADLE_PLUGIN =
        "https://github.com/KeepSafe/dexcount-gradle-plugin";
    @NonNull public static final String GRADLE_VERSIONS_PLUGIN =
        "https://github.com/ben-manes/gradle-versions-plugin";

    private HomepageUrls() {
      throw new RuntimeException("No instances");
    }
  }

  public static final class LicenseLocations {
    // Add an underscore to keep this name on top
    @NonNull private static final String _BASE = "licenses/";
    @NonNull public static final String ANDROID_SUPPORT = _BASE + "androidsupport";
    @NonNull public static final String ANDROID = _BASE + "android";
    @NonNull public static final String GOOGLE_PLAY = _BASE + "";
    @NonNull public static final String PYDROID = _BASE + "pydroid";
    @NonNull public static final String AUTO_VALUE = _BASE + "autovalue";
    @NonNull public static final String RETROFIT = _BASE + "retrofit";
    @NonNull public static final String FIREBASE = _BASE + "firebase";
    @NonNull public static final String DAGGER2 = _BASE + "dagger2";
    @NonNull public static final String ANDROID_CHECKOUT = _BASE + "androidcheckout";
    @NonNull public static final String LEAK_CANARY = _BASE + "leakcanary";
    @NonNull public static final String RXJAVA = _BASE + "rxjava";
    @NonNull public static final String RXANDROID = _BASE + "rxandroid";
    @NonNull public static final String FAST_ADAPTER = _BASE + "fastadapter";
    @NonNull public static final String ERROR_PRONE = _BASE + "errorprone";
    @NonNull public static final String TAP_TARGET_VIEW = _BASE + "taptargetview";
    @NonNull public static final String TIMBER = _BASE + "timber";
    @NonNull public static final String RETROLAMBDA = _BASE + "retrolambda";
    @NonNull public static final String GRADLE_RETROLAMBDA = _BASE + "gradle-retrolambda";
    @NonNull public static final String DEXCOUNT_GRADLE_PLUGIN = _BASE + "dexcount-gradle-plugin";
    @NonNull public static final String GRADLE_VERSIONS_PLUGIN = _BASE + "gradle-versions-plugin";

    private LicenseLocations() {
      throw new RuntimeException("No instances");
    }
  }
}
