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

package com.pyamsoft.pydroid.inject;

import android.content.Context;
import android.support.annotation.NonNull;
import com.pyamsoft.pydroid.base.PersistLoader;
import com.pyamsoft.pydroid.version.ApiModule;
import com.pyamsoft.pydroid.version.VersionCheckPresenter;
import javax.inject.Inject;
import javax.inject.Provider;

public class LicenseCheckPresenterLoader extends PersistLoader<VersionCheckPresenter> {
  private final boolean isDebugMode;
  @NonNull private final String projectName;
  @Inject Provider<VersionCheckPresenter> presenterProvider;

  public LicenseCheckPresenterLoader(@NonNull Context context, boolean isDebugMode,
      @NonNull String projectName) {
    super(context);
    this.isDebugMode = isDebugMode;
    this.projectName = projectName;
  }

  @NonNull @Override public VersionCheckPresenter loadPersistent() {
    Singleton.Dagger.with(getContext())
        .plusApiComponent(new ApiModule(isDebugMode, projectName))
        .plusVersionCheckComponent()
        .inject(this);
    return presenterProvider.get();
  }
}
