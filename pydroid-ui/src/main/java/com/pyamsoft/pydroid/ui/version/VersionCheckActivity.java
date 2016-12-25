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

package com.pyamsoft.ui.version;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.pyamsoft.pydroid.BuildConfigChecker;
import com.pyamsoft.pydroid.VersionCheckLoaderCallback;
import com.pyamsoft.pydroid.util.AppUtil;
import com.pyamsoft.pydroid.util.PersistentCache;
import com.pyamsoft.pydroid.version.VersionCheckPresenter;
import com.pyamsoft.pydroid.version.VersionCheckProvider;
import com.pyamsoft.ui.ads.AdvertisementActivity;
import timber.log.Timber;

public abstract class VersionCheckActivity extends AdvertisementActivity
    implements VersionCheckPresenter.View, VersionCheckProvider {

  @NonNull private static final String KEY_HAS_CHECKED_LICENSE = "key_has_already_checked_license";
  @NonNull private static final String KEY_VERSION_PRESENTER = "__key_version_presenter";
  @SuppressWarnings("WeakerAccess") VersionCheckPresenter presenter;
  private long loadedKey;
  private VersionCheckLoaderCallback loaderCallback;

  @CheckResult private boolean isVersionCheckEnabled() {
    // Always enabled for release builds
    return !BuildConfigChecker.getInstance().isDebugMode() || shouldCheckVersion();
  }

  @CheckResult protected boolean shouldCheckVersion() {
    return true;
  }

  @CallSuper @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    loaderCallback = new VersionCheckLoaderCallback() {

      @Override public void onPersistentLoaded(@NonNull VersionCheckPresenter persist) {
        presenter = persist;
      }
    };

    if (savedInstanceState != null) {
      loaderCallback.setLicenseChecked(
          savedInstanceState.getBoolean(KEY_HAS_CHECKED_LICENSE, false));
    }

    loadedKey =
        PersistentCache.get().load(KEY_VERSION_PRESENTER, savedInstanceState, loaderCallback);
  }

  @CallSuper @Override protected void onDestroy() {
    super.onDestroy();
    if (!isChangingConfigurations()) {
      PersistentCache.get().unload(loadedKey);
    }
  }

  @CallSuper @Override protected void onSaveInstanceState(Bundle outState) {
    PersistentCache.get()
        .saveKey(outState, KEY_VERSION_PRESENTER, loadedKey, VersionCheckPresenter.class);
    outState.putBoolean(KEY_HAS_CHECKED_LICENSE, loaderCallback.isLicenseChecked());
    super.onSaveInstanceState(outState);
  }

  @CallSuper @Override protected void onStart() {
    super.onStart();
    presenter.bindView(this);

    if (isVersionCheckEnabled()) {
      if (!loaderCallback.isLicenseChecked()) {
        presenter.checkForUpdates(getCurrentApplicationVersion());
      }
    }
  }

  @CallSuper @Override protected void onStop() {
    super.onStop();
    presenter.unbindView();
  }

  @Override public void onVersionCheckFinished() {
    Timber.d("License check finished, mark");
    loaderCallback.setLicenseChecked(true);
  }

  @Override public void onUpdatedVersionFound(int currentVersionCode, int updatedVersionCode) {
    Timber.d("Updated version found. %d => %d", currentVersionCode, updatedVersionCode);
    AppUtil.guaranteeSingleDialogFragment(getSupportFragmentManager(),
        VersionUpgradeDialog.newInstance(provideApplicationName(), currentVersionCode,
            updatedVersionCode), VersionUpgradeDialog.TAG);
  }
}
