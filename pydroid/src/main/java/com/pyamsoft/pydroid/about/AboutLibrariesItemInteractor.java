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
import com.pyamsoft.pydroid.helper.Checker;
import io.reactivex.Single;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import timber.log.Timber;

public class AboutLibrariesItemInteractor {

  @SuppressWarnings("WeakerAccess") @NonNull final Map<String, String> cachedLicenses;
  @SuppressWarnings("WeakerAccess") @NonNull private final LicenseProvider licenseProvider;
  @SuppressWarnings("WeakerAccess") @NonNull private final AssetManager assetManager;
  @NonNull private final Context appContext;

  public AboutLibrariesItemInteractor(@NonNull Context context,
      @NonNull LicenseProvider licenseProvider) {
    appContext = Checker.checkNonNull(context).getApplicationContext();
    this.licenseProvider = Checker.checkNonNull(licenseProvider);
    assetManager = context.getAssets();
    cachedLicenses = new HashMap<>();
  }

  /**
   * public
   */
  @CheckResult @NonNull Single<String> loadLicenseText(@NonNull AboutLibrariesModel model) {
    return Single.fromCallable(() -> {
      AboutLibrariesModel license = Checker.checkNonNull(model);
      String name = license.name();
      if (cachedLicenses.containsKey(name)) {
        Timber.d("Fetch from cache for name: %s", name);
        return cachedLicenses.get(name);
      } else {
        Timber.d("Load from asset location: %s (%s)", name, license.license());
        return loadNewLicense(name, license.license());
      }
    }).doOnSuccess(license -> {
      String name = Checker.checkNonNull(model).name();
      Timber.d("Put license into cache for model: %s", name);
      cachedLicenses.put(name, license);
    });
  }

  @SuppressWarnings("WeakerAccess") @SuppressLint("NewApi") @NonNull @CheckResult
  String loadNewLicense(@NonNull String licenseName, @NonNull String licenseLocation) {
    licenseName = Checker.checkNonNull(licenseName);
    licenseLocation = Checker.checkNonNull(licenseLocation);

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
