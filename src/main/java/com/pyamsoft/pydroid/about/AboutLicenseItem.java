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

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

class AboutLicenseItem {

  @NonNull private final String name;
  @NonNull private final String homepageUrl;
  @NonNull private final String licenseLocation;

  AboutLicenseItem(@NonNull String name, @NonNull String homepageUrl,
      @NonNull String licenseLocation) {
    this.name = name;
    this.homepageUrl = homepageUrl;
    this.licenseLocation = licenseLocation;
  }

  @NonNull @CheckResult String getName() {
    return name;
  }

  @NonNull @CheckResult String getHomepageUrl() {
    return homepageUrl;
  }

  @NonNull @CheckResult String getLicenseLocation() {
    return licenseLocation;
  }
}
