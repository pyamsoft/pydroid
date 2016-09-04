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

import android.support.annotation.CheckResult;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import com.pyamsoft.pydroid.app.about.AboutLibrariesFragment;
import com.pyamsoft.pydroid.app.about.Licenses;
import com.pyamsoft.pydroid.app.activity.AdvertisementActivity;
import com.pyamsoft.pydroid.app.support.RatingDialog;
import timber.log.Timber;

public abstract class ActionBarSettingsPreferenceFragment extends ActionBarPreferenceFragment {

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
}
