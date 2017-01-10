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

package com.pyamsoft.pydroid.ui.version;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.pyamsoft.pydroid.BuildConfigChecker;
import com.pyamsoft.pydroid.VersionCheckPresenterLoader;
import com.pyamsoft.pydroid.cache.PersistentCache;
import com.pyamsoft.pydroid.ui.ads.AdvertisementActivity;
import com.pyamsoft.pydroid.util.AppUtil;
import com.pyamsoft.pydroid.version.VersionCheckPresenter;
import com.pyamsoft.pydroid.version.VersionCheckProvider;
import timber.log.Timber;

public abstract class VersionCheckActivity extends AdvertisementActivity
    implements VersionCheckPresenter.View, VersionCheckProvider {

  @NonNull private static final String KEY_VERSION_PRESENTER = "__key_version_presenter";
  @SuppressWarnings("WeakerAccess") VersionCheckPresenter presenter;

  @CheckResult private boolean isVersionCheckEnabled() {
    // Always enabled for release builds
    return !BuildConfigChecker.getInstance().isDebugMode() || shouldCheckVersion();
  }

  @CheckResult protected boolean shouldCheckVersion() {
    return true;
  }

  @CallSuper @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    presenter =
        PersistentCache.load(this, KEY_VERSION_PRESENTER, new VersionCheckPresenterLoader());
  }

  @CallSuper @Override protected void onStart() {
    super.onStart();
    presenter.bindView(this);

    if (isVersionCheckEnabled()) {
      presenter.checkForUpdates(getCurrentApplicationVersion());
    }
  }

  @CallSuper @Override protected void onStop() {
    super.onStop();
    presenter.unbindView();
  }

  @Override public void onVersionCheckFinished() {
    Timber.d("License check finished, mark");
  }

  @Override public void onUpdatedVersionFound(int currentVersionCode, int updatedVersionCode) {
    Timber.d("Updated version found. %d => %d", currentVersionCode, updatedVersionCode);
    AppUtil.guaranteeSingleDialogFragment(getSupportFragmentManager(),
        VersionUpgradeDialog.newInstance(provideApplicationName(), currentVersionCode,
            updatedVersionCode), VersionUpgradeDialog.TAG);
  }
}
