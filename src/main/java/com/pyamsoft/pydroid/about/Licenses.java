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
    createItem(Names.ANDROID, HomepageUrls.ANDROID, LicenseLocations.ANDROID);
    createItem(Names.ANDROID_SUPPORT, HomepageUrls.ANDROID_SUPPORT,
        LicenseLocations.ANDROID_SUPPORT);
    createItem(Names.GOOGLE_PLAY, HomepageUrls.GOOGLE_PLAY, LicenseLocations.GOOGLE_PLAY);
  }

  public static void create(@NonNull String name, @NonNull String homepageUrl,
      @NonNull String licenseLocation) {
    INSTANCE.createItem(name, homepageUrl, licenseLocation);
  }

  static void forEach(@NonNull Action1<AboutLicenseItem> action) {
    INSTANCE.forEachItem(action);
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

    private Names() {
      throw new RuntimeException("No instances");
    }
  }

  @SuppressWarnings("WeakerAccess") static final class HomepageUrls {
    @NonNull static final String ANDROID = "https://source.android.com";
    @NonNull static final String ANDROID_SUPPORT = "https://source.android.com";
    @NonNull static final String GOOGLE_PLAY =
        "https://developers.google.com/android/guides/overview";

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

    private LicenseLocations() {
      throw new RuntimeException("No instances");
    }
  }
}
