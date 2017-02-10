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

package com.pyamsoft.pydroid.ui;

import android.support.annotation.NonNull;
import com.pyamsoft.pydroid.about.Licenses;

import static com.pyamsoft.pydroid.about.Licenses.LicenseLocations._BASE;

final class RxLicenses {

  private RxLicenses() {
    throw new RuntimeException("No instances");
  }

  static void addLicenses() {
    Licenses.create(Names.RXJAVA, HomepageUrls.RXJAVA, LicenseLocations.RXJAVA);
    Licenses.create(Names.RXANDROID, HomepageUrls.RXANDROID, LicenseLocations.RXANDROID);
  }

  private static final class Names {

    @NonNull static final String RXJAVA = "RxJava";
    @NonNull static final String RXANDROID = "RxAndroid";

    private Names() {
      throw new RuntimeException("No instances");
    }
  }

  private static final class HomepageUrls {

    @NonNull static final String RXJAVA = "https://github.com/ReactiveX/RxJava";
    @NonNull static final String RXANDROID = "https://github.com/ReactiveX/RxAndroid";

    private HomepageUrls() {
      throw new RuntimeException("No instances");
    }
  }

  private static final class LicenseLocations {

    @NonNull static final String RXJAVA = _BASE + "rxjava";
    @NonNull static final String RXANDROID = _BASE + "rxandroid";

    private LicenseLocations() {
      throw new RuntimeException("No instances");
    }
  }
}
