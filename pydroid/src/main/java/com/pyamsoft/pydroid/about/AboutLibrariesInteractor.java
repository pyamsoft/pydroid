/*
 * Copyright 2017 Peter Kenji Yamanaka
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

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Build;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.pyamsoft.pydroid.helper.Checker;
import io.reactivex.Observable;
import io.reactivex.Single;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import timber.log.Timber;

public class AboutLibrariesInteractor {

  @SuppressWarnings("WeakerAccess") @NonNull final List<AboutLibrariesModel> licenses;
  @SuppressWarnings("WeakerAccess") @NonNull final Map<String, String> cachedLicenses;
  @SuppressWarnings("WeakerAccess") @NonNull private final AssetManager assetManager;

  @SuppressWarnings("WeakerAccess") public AboutLibrariesInteractor(@NonNull Context context,
      @NonNull List<AboutLibrariesModel> licenses) {
    this.licenses = Collections.unmodifiableList(licenses);
    assetManager = context.getApplicationContext().getAssets();
    cachedLicenses = new HashMap<>();
  }

  /**
   * public
   */
  @NonNull @CheckResult Observable<AboutLibrariesModel> loadLicenses() {
    return Observable.defer(() -> Observable.fromIterable(licenses))
        .toSortedList((o1, o2) -> o1.name().compareTo(o2.name()))
        .toObservable()
        .concatMap(Observable::fromIterable)
        .map(aboutLibrariesModel -> AboutLibrariesModel.create(aboutLibrariesModel.name(),
            aboutLibrariesModel.homepage(), loadLicenseText(aboutLibrariesModel)));
  }

  @SuppressWarnings("WeakerAccess") @CheckResult @NonNull String loadLicenseText(
      @NonNull AboutLibrariesModel model) {
    return Single.fromCallable(() -> {
      AboutLibrariesModel license = Checker.checkNonNull(model);
      String name = license.name();
      if (cachedLicenses.containsKey(name)) {
        Timber.d("Fetch from cache for name: %s", name);
        return cachedLicenses.get(name);
      } else {
        if (model.customContent().isEmpty()) {
          Timber.d("Load from asset location: %s (%s)", name, license.license());
          return loadNewLicense(license.license());
        } else {
          Timber.d("License: %s provides custom content", name);
          return model.customContent();
        }
      }
    }).doOnSuccess(license -> {
      String name = Checker.checkNonNull(model).name();
      if (!license.isEmpty()) {
        Timber.d("Put license into cache for model: %s", name);
        cachedLicenses.put(name, license);
      }
    }).blockingGet();
  }

  @SuppressWarnings("WeakerAccess") @NonNull @CheckResult String loadNewLicense(
      @NonNull String licenseLocation) throws IOException {
    licenseLocation = Checker.checkNonNull(licenseLocation);

    if (licenseLocation.isEmpty()) {
      Timber.w("Empty license passed");
      return "";
    }

    String licenseText;
    InputStream fileInputStream = null;
    try {
      fileInputStream = assetManager.open(licenseLocation);
      // Standard Charsets is only KitKat, add this extra check to support Home Button
      final InputStreamReader inputStreamReader;
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
      } else {
        inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
      }

      BufferedReader br = new BufferedReader(inputStreamReader);
      final StringBuilder text = new StringBuilder();
      String line = br.readLine();
      while (line != null) {
        text.append(line).append('\n');
        line = br.readLine();
      }
      br.close();

      licenseText = text.toString();
    } finally {
      if (fileInputStream != null) {
        fileInputStream.close();
      }
    }

    return licenseText;
  }
}
