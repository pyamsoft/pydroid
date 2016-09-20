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

import android.content.Context;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import javax.inject.Inject;
import rx.Observable;
import timber.log.Timber;

class AboutLibrariesInteractorImpl implements AboutLibrariesInteractor {

  @SuppressWarnings("WeakerAccess") @NonNull final Context appContext;
  @SuppressWarnings("WeakerAccess") @NonNull final HashMap<Licenses, String> cachedLicenses;

  @Inject AboutLibrariesInteractorImpl(@NonNull Context context) {
    this.appContext = context.getApplicationContext();
    cachedLicenses = new HashMap<>();
  }

  @Override public void clearCache() {
    cachedLicenses.clear();
  }

  @SuppressWarnings("WeakerAccess") @VisibleForTesting @NonNull @CheckResult
  Observable<String> loadNewLicense(@NonNull Licenses licenses) {
    return Observable.defer(() -> {
      if (licenses.id() == Licenses.Id.EMPTY) {
        Timber.w("Empty license passed");
        return Observable.just("");
      }

      if (licenses.id() == Licenses.Id.GOOGLE_PLAY_SERVICES) {
        Timber.d("License is Google Play services");
        final String googleOpenSourceLicenses =
            PYDroidApplication.licenses(appContext).provideGoogleOpenSourceLicenses();
        final Observable<String> result = Observable.just(
            googleOpenSourceLicenses == null ? "Unable to load Google Play Open Source Licenses"
                : googleOpenSourceLicenses);
        Timber.i("Finished loading Google Play services license");
        return result;
      }

      String licenseText;
      final StringBuilder text = new StringBuilder();
      try (
          final InputStream fileInputStream = appContext.getAssets().open(licenses.location());
          final BufferedReader br = new BufferedReader(
              new InputStreamReader(fileInputStream, StandardCharsets.UTF_8))) {
        String line = br.readLine();
        while (line != null) {
          text.append(line).append('\n');
          line = br.readLine();
        }
        licenseText = text.toString();
      } catch (IOException e) {
        e.printStackTrace();
        licenseText = "Could not load license text";
      }

      return Observable.just(licenseText);
    });
  }

  @NonNull @Override public Observable<String> loadLicenseText(@NonNull Licenses licenses) {
    return Observable.defer(() -> {
      if (cachedLicenses.containsKey(licenses)) {
        return Observable.just(cachedLicenses.get(licenses));
      } else {
        return loadNewLicense(licenses);
      }
    }).map(s -> {
      if (!cachedLicenses.containsKey(licenses)) {
        cachedLicenses.put(licenses, s);
      }

      return s;
    });
  }
}
