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

package com.pyamsoft.pydroid.ui.app.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.CheckResult;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.XmlRes;
import android.support.v4.app.FragmentActivity;
import android.support.v7.preference.Preference;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.pyamsoft.pydroid.ui.PYDroidInjector;
import com.pyamsoft.pydroid.ui.R;
import com.pyamsoft.pydroid.ui.about.AboutLibrariesFragment;
import com.pyamsoft.pydroid.ui.rating.RatingDialog;
import com.pyamsoft.pydroid.ui.social.Linker;
import com.pyamsoft.pydroid.ui.version.VersionCheckActivity;
import com.pyamsoft.pydroid.ui.version.VersionUpgradeDialog;
import com.pyamsoft.pydroid.util.DialogUtil;
import com.pyamsoft.pydroid.version.VersionCheckPresenter;
import com.pyamsoft.pydroid.version.VersionCheckProvider;
import java.util.Locale;
import timber.log.Timber;

public abstract class ActionBarSettingsPreferenceFragment extends ActionBarPreferenceFragment {

  VersionCheckPresenter presenter;
  private Toast toast;

  @CallSuper @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    PYDroidInjector.with(getContext()).plusAppComponent().inject(this);
  }

  @SuppressLint("ShowToast") @CallSuper @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    toast = Toast.makeText(getContext(), "Checking for updates...", Toast.LENGTH_SHORT);
    return super.onCreateView(inflater, container, savedInstanceState);
  }

  @Override public final void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
    @XmlRes final int xmlResId = getPreferenceXmlResId();
    if (xmlResId != 0) {
      addPreferencesFromResource(xmlResId);
    }
    addPreferencesFromResource(R.xml.pydroid);

    final Preference applicationSettings = findPreference("application_settings");
    if (applicationSettings != null) {
      applicationSettings.setTitle(
          String.format(Locale.getDefault(), "%s Settings", getApplicationName()));
    }
  }

  @CallSuper @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    final Preference upgradeInfo = findPreference(getString(R.string.upgrade_info_key));
    if (upgradeInfo != null) {
      if (hideUpgradeInformation()) {
        upgradeInfo.setVisible(false);
      } else {
        upgradeInfo.setOnPreferenceClickListener(preference -> {
          onShowChangelogClicked();
          return true;
        });
      }
    }

    final Preference showAboutLicenses = findPreference(getString(R.string.about_license_key));
    showAboutLicenses.setOnPreferenceClickListener(preference -> {
      onLicenseItemClicked();
      return true;
    });

    final Preference checkVersion = findPreference(getString(R.string.check_version_key));
    checkVersion.setOnPreferenceClickListener(preference -> {
      onCheckForUpdatesClicked();
      return true;
    });

    final Preference clearAll = findPreference(getString(R.string.clear_all_key));
    if (clearAll != null) {
      if (hideClearAll()) {
        clearAll.setVisible(false);
      } else {
        clearAll.setOnPreferenceClickListener(preference -> {
          onClearAllClicked();
          return true;
        });
      }
    }

    final Preference rateApplication = findPreference(getString(R.string.rating_key));
    rateApplication.setOnPreferenceClickListener(preference -> {
      Linker.with(preference.getContext()).clickAppPage(preference.getContext().getPackageName());
      return true;
    });
  }

  @CallSuper @Override public void onStop() {
    super.onStop();
    presenter.stop();
  }

  /**
   * Logs when the Clear All option is clicked, override to use unique implementation
   */
  protected void onClearAllClicked() {
    Timber.d("Clear all preferences clicked");
  }

  /**
   * Shows a page for Open Source licenses, override or extend to use unique implementation
   */
  protected void onLicenseItemClicked() {
    Timber.d("Show about licenses fragment");
    AboutLibrariesFragment.show(getActivity(), getRootViewContainer(), isLastOnBackStack());
  }

  final void onShowChangelogClicked() {
    final FragmentActivity activity = getActivity();
    if (activity instanceof RatingDialog.ChangeLogProvider) {
      onShowChangelogClicked((RatingDialog.ChangeLogProvider) activity);
    } else {
      throw new ClassCastException("Activity is not a change log provider");
    }
  }

  /**
   * Shows the changelog, override or extend to use unique implementation
   */
  protected void onShowChangelogClicked(@NonNull RatingDialog.ChangeLogProvider provider) {
    RatingDialog.showRatingDialog(getActivity(), provider, true);
  }

  final void onCheckForUpdatesClicked() {
    onCheckForUpdatesClicked(presenter);
  }

  /**
   * Checks the server for updates, override to use a custom behavior
   */
  protected void onCheckForUpdatesClicked(@NonNull VersionCheckPresenter presenter) {
    toast.show();
    presenter.forceCheckForUpdates(getContext().getPackageName(), getCurrentApplicationVersion(),
        new VersionCheckPresenter.UpdateCheckCallback() {
          @Override public void onVersionCheckFinished() {
            Timber.d("License check finished, mark");
          }

          @Override public void onUpdatedVersionFound(int oldVersionCode, int updatedVersionCode) {
            Timber.d("Updated version found. %d => %d", oldVersionCode, updatedVersionCode);
            DialogUtil.guaranteeSingleDialogFragment(getActivity(),
                VersionUpgradeDialog.newInstance(provideApplicationName(), oldVersionCode,
                    updatedVersionCode), VersionUpgradeDialog.TAG);
          }
        });
  }

  @NonNull @CheckResult protected AboutLibrariesFragment.BackStackState isLastOnBackStack() {
    return AboutLibrariesFragment.BackStackState.NOT_LAST;
  }

  @NonNull @CheckResult final String provideApplicationName() {
    return getVersionedActivity().provideApplicationName();
  }

  @CheckResult final int getCurrentApplicationVersion() {
    return getVersionedActivity().getCurrentApplicationVersion();
  }

  @CheckResult @NonNull private VersionCheckProvider getVersionedActivity() {
    Activity activity = getActivity();
    if (activity instanceof VersionCheckActivity) {
      return (VersionCheckProvider) activity;
    } else {
      throw new IllegalStateException("Activity is not VersionCheckActivity");
    }
  }

  @CheckResult @XmlRes protected int getPreferenceXmlResId() {
    return 0;
  }

  @CheckResult protected boolean hideUpgradeInformation() {
    return false;
  }

  @CheckResult protected boolean hideClearAll() {
    return false;
  }

  @CheckResult @IdRes protected abstract int getRootViewContainer();

  @CheckResult @NonNull protected abstract String getApplicationName();
}
