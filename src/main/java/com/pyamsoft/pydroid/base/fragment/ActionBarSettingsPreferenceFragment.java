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

package com.pyamsoft.pydroid.base.fragment;

import android.support.annotation.CheckResult;
import android.support.v4.app.FragmentActivity;
import com.pyamsoft.pydroid.base.activity.ActivityBase;
import com.pyamsoft.pydroid.support.RatingDialog;
import timber.log.Timber;

public abstract class ActionBarSettingsPreferenceFragment extends ActionBarPreferenceFragment {

  @CheckResult protected boolean showChangelog() {
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

  @CheckResult protected boolean toggleAdVisibility(boolean b) {
    final ActivityBase activity = (ActivityBase) getActivity();
    if (b) {
      Timber.d("Turn on ads");
      activity.showAd();
    } else {
      Timber.d("Turn off ads");
      activity.hideAd();
    }
    return true;
  }
}
