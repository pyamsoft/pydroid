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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Build;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import android.support.annotation.VisibleForTesting;
import android.support.annotation.WorkerThread;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import rx.Observable;
import timber.log.Timber;

class AboutLibrariesInteractor {

  @SuppressWarnings("WeakerAccess") @NonNull final Map<String, String> cachedLicenses;
  @SuppressWarnings("WeakerAccess") @NonNull private final LicenseProvider licenseProvider;
  @SuppressWarnings("WeakerAccess") @NonNull private final AssetManager assetManager;
  @NonNull private final Context appContext;

  AboutLibrariesInteractor(@NonNull Context context, @NonNull LicenseProvider licenseProvider) {
    appContext = context.getApplicationContext();
    assetManager = context.getAssets();
    this.licenseProvider = licenseProvider;
    cachedLicenses = new HashMap<>();
  }

  void clearCache() {
    cachedLicenses.clear();
  }

  @CheckResult @NonNull Observable<String> loadLicenseText(@NonNull AboutLicenseModel license) {
    return Observable.fromCallable(() -> {
      if (cachedLicenses.containsKey(license.name())) {
        Timber.d("Fetch from cache");
        return cachedLicenses.get(license.name());
      } else {
        Timber.d("Load from asset location");
        final String licenseText = loadNewLicense(license.name(), license.license());
        Timber.d("Put into cache");
        cachedLicenses.put(license.name(), licenseText);
        return licenseText;
      }
    });
  }

  /**
   * @hide
   */
  @RestrictTo(RestrictTo.Scope.SUBCLASSES) @SuppressLint("NewApi") @SuppressWarnings("WeakerAccess")
  @VisibleForTesting @NonNull @CheckResult @WorkerThread String loadNewLicense(
      @NonNull String licenseName, @NonNull String licenseLocation) {
    if (licenseLocation.isEmpty()) {
      Timber.w("Empty license passed");
      return "";
    }

    if (Licenses.Names.GOOGLE_PLAY.equals(licenseName)) {
      Timber.d("License is Google Play services");
      final String googleOpenSourceLicenses =
          licenseProvider.provideGoogleOpenSourceLicenses(appContext);
      final String result =
          googleOpenSourceLicenses == null ? "Unable to load Google Play Open Source Licenses"
              : googleOpenSourceLicenses;
      Timber.i("Finished loading Google Play services license");
      return result;
    }

    String licenseText;
    try (InputStream fileInputStream = assetManager.open(licenseLocation)) {

      // Standard Charsets is only KitKat, add this extra check to support Home Button
      final InputStreamReader inputStreamReader;
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
      } else {
        inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
      }

      try (BufferedReader br = new BufferedReader(inputStreamReader)) {
        final StringBuilder text = new StringBuilder();
        String line = br.readLine();
        while (line != null) {
          text.append(line).append('\n');
          line = br.readLine();
        }
        licenseText = text.toString();
      }
    } catch (IOException e) {
      Timber.e(e, "onError");
      licenseText = "Could not load license text";
    }

    return licenseText;
  }
}
