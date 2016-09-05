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

package com.pyamsoft.pydroid.app.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.CheckResult;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.pyamsoft.pydroid.about.AboutLibrariesFragment;
import com.pyamsoft.pydroid.app.activity.AdvertisementActivity;
import com.pyamsoft.pydroid.base.PersistLoader;
import com.pyamsoft.pydroid.inject.LicenseCheckPresenterLoader;
import com.pyamsoft.pydroid.model.Licenses;
import com.pyamsoft.pydroid.support.RatingDialog;
import com.pyamsoft.pydroid.util.AppUtil;
import com.pyamsoft.pydroid.util.PersistentCache;
import com.pyamsoft.pydroid.version.VersionCheckPresenter;
import com.pyamsoft.pydroid.version.VersionCheckProvider;
import com.pyamsoft.pydroid.version.VersionUpgradeDialog;
import timber.log.Timber;

public abstract class ActionBarSettingsPreferenceFragment extends ActionBarPreferenceFragment
    implements VersionCheckPresenter.View, VersionCheckProvider {

  @NonNull private static final String KEY_PRESENTER = "key_license_check_presenter";
  VersionCheckPresenter presenter;
  private long loadedKey;
  private Toast toast;

  @SuppressWarnings("SameReturnValue") @CheckResult protected boolean showChangelog() {
    final FragmentActivity activity = getActivity();
    if (activity instanceof RatingDialog.ChangeLogProvider) {
      final RatingDialog.ChangeLogProvider provider = (RatingDialog.ChangeLogProvider) activity;
      RatingDialog.showRatingDialog(activity, provider, true);
    } else {
      throw new ClassCastException("Activity is not a change log provider");
    }
    return true;
  }

  @CheckResult protected boolean toggleAdVisibility(Object object) {
    if (object instanceof Boolean) {
      final boolean b = (boolean) object;
      return toggleAdVisibility(b);
    }
    return false;
  }

  @SuppressWarnings("WeakerAccess") @CheckResult protected boolean toggleAdVisibility(boolean b) {
    final FragmentActivity activity = getActivity();
    if (activity instanceof AdvertisementActivity) {
      final AdvertisementActivity advertisementActivity = (AdvertisementActivity) getActivity();
      if (b) {
        Timber.d("Turn on ads");
        advertisementActivity.showAd();
      } else {
        Timber.d("Turn off ads");
        advertisementActivity.hideAd();
      }
      return true;
    } else {
      Timber.e("Activity is not AdvertisementActivity");
      return false;
    }
  }

  @CheckResult protected boolean showAboutLicensesFragment(@IdRes int containerId,
      @NonNull AboutLibrariesFragment.Styling styling, @NonNull Licenses... licenses) {
    Timber.d("Show about licenses fragment");
    AboutLibrariesFragment.show(getActivity(), containerId, styling, licenses);
    return true;
  }

  @CheckResult protected boolean checkForUpdate() {
    toast.cancel();
    toast.show();
    presenter.checkForUpdates(getCurrentApplicationVersion());
    return true;
  }

  @CallSuper @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    loadedKey = PersistentCache.load(KEY_PRESENTER, savedInstanceState,
        new PersistLoader.Callback<VersionCheckPresenter>() {
          @NonNull @Override public PersistLoader<VersionCheckPresenter> createLoader() {
            return new LicenseCheckPresenterLoader(getContext().getApplicationContext());
          }

          @Override public void onPersistentLoaded(@NonNull VersionCheckPresenter persist) {
            presenter = persist;
          }
        });
  }

  @SuppressLint("ShowToast") @CallSuper @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    toast = Toast.makeText(getContext(), "Checking for updates...", Toast.LENGTH_SHORT);
    return super.onCreateView(inflater, container, savedInstanceState);
  }

  @CallSuper @Override public void onDestroy() {
    super.onDestroy();
    if (!getActivity().isChangingConfigurations()) {
      PersistentCache.unload(loadedKey);
    }
  }

  @CallSuper @Override public void onSaveInstanceState(Bundle outState) {
    PersistentCache.saveKey(KEY_PRESENTER, outState, loadedKey);
    super.onSaveInstanceState(outState);
  }

  @CallSuper @Override public void onStart() {
    super.onStart();
    presenter.bindView(this);
  }

  @CallSuper @Override public void onStop() {
    super.onStop();
    presenter.unbindView();
  }

  @Override public void onLicenseCheckFinished() {
    Timber.d("License check finished, mark");
  }

  @Override public void onUpdatedVersionFound(int currentVersionCode, int updatedVersionCode) {
    Timber.d("Updated version found. %d => %d", currentVersionCode, updatedVersionCode);
    AppUtil.guaranteeSingleDialogFragment(getFragmentManager(),
        VersionUpgradeDialog.newInstance(provideApplicationName(), currentVersionCode,
            updatedVersionCode), VersionUpgradeDialog.TAG);
  }

  @CheckResult @NonNull VersionCheckProvider getVersionCheckProvider() {
    final FragmentActivity activity = getActivity();
    if (activity instanceof VersionCheckProvider) {
      return (VersionCheckProvider) activity;
    } else {
      throw new RuntimeException("No version check provider in activity");
    }
  }

  @NonNull @Override public String provideApplicationName() {
    return getVersionCheckProvider().provideApplicationName();
  }

  @Override public int getCurrentApplicationVersion() {
    return getVersionCheckProvider().getCurrentApplicationVersion();
  }
}
