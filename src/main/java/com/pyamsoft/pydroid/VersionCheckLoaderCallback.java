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

package com.pyamsoft.pydroid;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.pyamsoft.pydroid.app.PersistLoader;
import com.pyamsoft.pydroid.version.VersionCheckPresenter;

public abstract class VersionCheckLoaderCallback
    implements PersistLoader.Callback<VersionCheckPresenter> {

  private boolean licenseChecked;

  protected VersionCheckLoaderCallback() {
    licenseChecked = false;
  }

  @NonNull @Override public PersistLoader<VersionCheckPresenter> createLoader() {
    setLicenseChecked(false);
    return SingleInitContentProvider.getInstance()
        .getModule()
        .provideVersionCheckModule()
        .getLoader();
  }

  @CheckResult public boolean isLicenseChecked() {
    return licenseChecked;
  }

  public void setLicenseChecked(boolean licenseChecked) {
    this.licenseChecked = licenseChecked;
  }
}
