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

package com.pyamsoft.pydroid.lib;

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
import com.pyamsoft.pydroid.base.ActionBarPreferenceFragment;
import com.pyamsoft.pydroid.base.PersistLoader;
import com.pyamsoft.pydroid.model.Licenses;
import com.pyamsoft.pydroid.support.RatingDialog;
import com.pyamsoft.pydroid.util.AppUtil;
import com.pyamsoft.pydroid.util.PersistentCache;
import timber.log.Timber;

public abstract class ActionBarSettingsPreferenceFragment extends ActionBarPreferenceFragment
    implements VersionCheckPresenter.View, VersionCheckProvider {

  @NonNull private static final String KEY_LICENSE_PRESENTER = "key_license_presenter";
  @SuppressWarnings("WeakerAccess") VersionCheckPresenter presenter;
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
    if (activity instanceof DonationActivity) {
      final DonationActivity donationActivity = (DonationActivity) getActivity();
      if (b) {
        Timber.d("Turn on ads");
        donationActivity.showAd();
      } else {
        Timber.d("Turn off ads");
        donationActivity.hideAd();
      }
      return true;
    } else {
      Timber.e("Activity is not AdvertisementActivity");
      return false;
    }
  }

  @SuppressWarnings("SameReturnValue") @CheckResult
  protected boolean showAboutLicensesFragment(@IdRes int containerId,
      @NonNull AboutLibrariesFragment.Styling styling, @NonNull Licenses... licenses) {
    Timber.d("Show about licenses fragment");
    AboutLibrariesFragment.show(getActivity(), containerId, styling, isLastOnBackStack(), licenses);
    return true;
  }

  @NonNull @CheckResult protected AboutLibrariesFragment.BackStackState isLastOnBackStack() {
    return AboutLibrariesFragment.BackStackState.NOT_LAST;
  }

  @SuppressWarnings("SameReturnValue") @CheckResult protected boolean checkForUpdate() {
    toast.show();
    presenter.checkForUpdates(getCurrentApplicationVersion());
    return true;
  }

  @CallSuper @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    loadedKey = PersistentCache.get()
        .load(KEY_LICENSE_PRESENTER, savedInstanceState,
            new PersistLoader.Callback<VersionCheckPresenter>() {
              @NonNull @Override public PersistLoader<VersionCheckPresenter> createLoader() {
                return new VersionCheckPresenterLoader(getContext().getApplicationContext());
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
      PersistentCache.get().unload(loadedKey);
    }
  }

  @CallSuper @Override public void onSaveInstanceState(Bundle outState) {
    PersistentCache.get().saveKey(outState, KEY_LICENSE_PRESENTER, loadedKey);
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

  @Override public void onVersionCheckFinished() {
    Timber.d("License check finished, mark");
  }

  @Override public void onUpdatedVersionFound(int currentVersionCode, int updatedVersionCode) {
    Timber.d("Updated version found. %d => %d", currentVersionCode, updatedVersionCode);
    AppUtil.guaranteeSingleDialogFragment(getFragmentManager(),
        VersionUpgradeDialog.newInstance(provideApplicationName(), currentVersionCode,
            updatedVersionCode), VersionUpgradeDialog.TAG);
  }

  @CheckResult @NonNull private DonationActivity getDonationActivity() {
    final FragmentActivity activity = getActivity();
    if (activity instanceof DonationActivity) {
      return (DonationActivity) activity;
    } else {
      throw new ClassCastException("Cannot cast to Donation activity");
    }
  }

  @NonNull @Override public String provideApplicationName() {
    return getDonationActivity().provideApplicationName();
  }

  @Override public int getCurrentApplicationVersion() {
    return getDonationActivity().getCurrentApplicationVersion();
  }
}
