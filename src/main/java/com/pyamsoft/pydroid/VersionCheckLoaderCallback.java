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

import android.content.Context;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.pyamsoft.pydroid.app.PersistLoader;
import com.pyamsoft.pydroid.version.VersionCheckPresenter;
import com.pyamsoft.pydroid.version.VersionCheckPresenterLoader;
import javax.inject.Inject;

public abstract class VersionCheckLoaderCallback
    implements PersistLoader.Callback<VersionCheckPresenter> {

  @NonNull private final Context context;
  @SuppressWarnings("WeakerAccess") @Inject VersionCheckPresenterLoader loader;
  private boolean licenseChecked;

  protected VersionCheckLoaderCallback(@NonNull Context context) {
    this.context = context.getApplicationContext();
    licenseChecked = false;
  }

  @NonNull @Override public PersistLoader<VersionCheckPresenter> createLoader() {
    setLicenseChecked(false);
    PYDroidApplication.get(context.getApplicationContext())
        .provideComponent()
        .plusApiComponent()
        .plusVersionCheckComponent()
        .inject(this);
    return loader;
  }

  @CheckResult public boolean isLicenseChecked() {
    return licenseChecked;
  }

  public void setLicenseChecked(boolean licenseChecked) {
    this.licenseChecked = licenseChecked;
  }
}
