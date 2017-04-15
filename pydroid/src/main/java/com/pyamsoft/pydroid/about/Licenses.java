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

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Licenses {

  @NonNull private static final Licenses INSTANCE = new Licenses();

  @NonNull private final List<AboutLibrariesModel> licenses;

  private Licenses() {
    licenses = new ArrayList<>();
    addCommonLicenses();
  }

  public static void create(@NonNull String name, @NonNull String homepageUrl,
      @NonNull String licenseLocation) {
    INSTANCE.createItem(name, homepageUrl, licenseLocation);
  }

  @CheckResult @NonNull public static List<AboutLibrariesModel> getLicenses() {
    return Collections.unmodifiableList(INSTANCE.licenses);
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
    createItem(Names.ERROR_PRONE, HomepageUrls.ERROR_PRONE, LicenseLocations.ERROR_PRONE);
    createItem(Names.TIMBER, HomepageUrls.TIMBER, LicenseLocations.TIMBER);
    createItem(Names.RETROLAMBDA, HomepageUrls.RETROLAMBDA, LicenseLocations.RETROLAMBDA);
    createItem(Names.GRADLE_RETROLAMBDA, HomepageUrls.GRADLE_RETROLAMBDA,
        LicenseLocations.GRADLE_RETROLAMBDA);
    createItem(Names.GRADLE_VERSIONS_PLUGIN, HomepageUrls.GRADLE_VERSIONS_PLUGIN,
        LicenseLocations.GRADLE_VERSIONS_PLUGIN);
    createItem(Names.DEXCOUNT_GRADLE_PLUGIN, HomepageUrls.DEXCOUNT_GRADLE_PLUGIN,
        LicenseLocations.DEXCOUNT_GRADLE_PLUGIN);
    createItem(Names.GOOGLE_PLAY, HomepageUrls.GOOGLE_PLAY, LicenseLocations.GOOGLE_PLAY);
    createItem(Names.RXJAVA, HomepageUrls.RXJAVA, LicenseLocations.RXJAVA);
    createItem(Names.RXANDROID, HomepageUrls.RXANDROID, LicenseLocations.RXANDROID);
  }

  @VisibleForTesting @SuppressWarnings("WeakerAccess") void createItem(@NonNull String name,
      @NonNull String homepageUrl, @NonNull String licenseLocation) {
    final AboutLibrariesModel item = AboutLibrariesModel.create(name, homepageUrl, licenseLocation);
    licenses.add(item);
  }

  static final class Names {

    // We explicitly check for this one
    @NonNull static final String GOOGLE_PLAY = "Google Play Services";

    @NonNull static final String RXJAVA = "RxJava";
    @NonNull static final String RXANDROID = "RxAndroid";
    @NonNull static final String ANDROID = "Android";
    @NonNull static final String ANDROID_SUPPORT = "Android Support Libraries";
    @NonNull static final String PYDROID = "PYDroid";
    @NonNull static final String AUTO_VALUE = "AutoValue";
    @NonNull static final String RETROFIT = "Retrofit";
    @NonNull static final String ERROR_PRONE = "Error Prone";
    @NonNull static final String TIMBER = "Timber";
    @NonNull static final String RETROLAMBDA = "Retrolambda";
    @NonNull static final String GRADLE_RETROLAMBDA = "Gradle Retrolambda";
    @NonNull static final String DEXCOUNT_GRADLE_PLUGIN = "Dexcount Gradle Plugin";
    @NonNull static final String GRADLE_VERSIONS_PLUGIN = "Gradle Versions Plugin";

    private Names() {
      throw new RuntimeException("No instances");
    }
  }

  private static final class HomepageUrls {
    @NonNull static final String RXJAVA = "https://github.com/ReactiveX/RxJava";
    @NonNull static final String RXANDROID = "https://github.com/ReactiveX/RxAndroid";
    @NonNull static final String ANDROID = "https://source.android.com";
    @NonNull static final String ANDROID_SUPPORT = "https://source.android.com";
    @NonNull static final String GOOGLE_PLAY =
        "https://developers.google.com/android/guides/overview";
    @NonNull static final String PYDROID = "https://pyamsoft.github.io/pydroid";
    @NonNull static final String AUTO_VALUE = "https://github.com/google/auto";
    @NonNull static final String RETROFIT = "https://square.github.io/retrofit/";
    @NonNull static final String ERROR_PRONE = "https://github.com/google/onError-prone";
    @NonNull static final String TIMBER = "https://github.com/JakeWharton/timber";
    @NonNull static final String RETROLAMBDA = "https://github.com/orfjackal/retrolambda";
    @NonNull static final String GRADLE_RETROLAMBDA = "https://github.com/evant/gradle-retrolambda";
    @NonNull static final String DEXCOUNT_GRADLE_PLUGIN =
        "https://github.com/KeepSafe/dexcount-gradle-plugin";
    @NonNull static final String GRADLE_VERSIONS_PLUGIN =
        "https://github.com/ben-manes/gradle-versions-plugin";

    private HomepageUrls() {
      throw new RuntimeException("No instances");
    }
  }

  public static final class LicenseLocations {
    // Add an underscore to keep this name on top
    @NonNull public static final String _BASE = "licenses/";
    @NonNull static final String RXJAVA = _BASE + "rxjava";
    @NonNull static final String RXANDROID = _BASE + "rxandroid";
    @NonNull static final String ANDROID_SUPPORT = _BASE + "androidsupport";
    @NonNull static final String ANDROID = _BASE + "android";
    @NonNull static final String GOOGLE_PLAY = _BASE + "";
    @NonNull static final String PYDROID = _BASE + "pydroid";
    @NonNull static final String AUTO_VALUE = _BASE + "autovalue";
    @NonNull static final String RETROFIT = _BASE + "retrofit";
    @NonNull static final String ERROR_PRONE = _BASE + "errorprone";
    @NonNull static final String TIMBER = _BASE + "timber";
    @NonNull static final String RETROLAMBDA = _BASE + "retrolambda";
    @NonNull static final String GRADLE_RETROLAMBDA = _BASE + "gradle-retrolambda";
    @NonNull static final String DEXCOUNT_GRADLE_PLUGIN = _BASE + "dexcount-gradle-plugin";
    @NonNull static final String GRADLE_VERSIONS_PLUGIN = _BASE + "gradle-versions-plugin";

    private LicenseLocations() {
      throw new RuntimeException("No instances");
    }
  }
}
