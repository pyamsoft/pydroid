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
 *
 */

package com.pyamsoft.pydroid.ui;

import android.support.annotation.NonNull;
import com.pyamsoft.pydroid.about.Licenses;

import static com.pyamsoft.pydroid.about.Licenses.LicenseLocations._BASE;

final class UiLicenses {

  private UiLicenses() {
    throw new RuntimeException("No instances");
  }

  static void addLicenses() {
    Licenses.create(Names.LEAK_CANARY, HomepageUrls.LEAK_CANARY, LicenseLocations.LEAK_CANARY);
    Licenses.create(Names.FAST_ADAPTER, HomepageUrls.FAST_ADAPTER, LicenseLocations.FAST_ADAPTER);
  }

  private static final class Names {

    @NonNull static final String LEAK_CANARY = "Leak Canary";
    @NonNull static final String FAST_ADAPTER = "Fast Adapter";

    private Names() {
      throw new RuntimeException("No instances");
    }
  }

  private static final class HomepageUrls {

    @NonNull static final String LEAK_CANARY = "https://github.com/square/leakcanary";
    @NonNull static final String FAST_ADAPTER = "https://github.com/mikepenz/FastAdapter";

    private HomepageUrls() {
      throw new RuntimeException("No instances");
    }
  }

  private static final class LicenseLocations {

    @NonNull static final String LEAK_CANARY = _BASE + "leakcanary";
    @NonNull static final String FAST_ADAPTER = _BASE + "fastadapter";

    private LicenseLocations() {
      throw new RuntimeException("No instances");
    }
  }
}
