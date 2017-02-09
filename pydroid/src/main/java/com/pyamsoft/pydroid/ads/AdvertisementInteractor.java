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

package com.pyamsoft.pydroid.ads;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.pyamsoft.pydroid.PYDroidPreferences;
import com.pyamsoft.pydroid.tool.AsyncOffloader;
import com.pyamsoft.pydroid.tool.Offloader;
import timber.log.Timber;

public class AdvertisementInteractor {

  @SuppressWarnings("WeakerAccess") static final int MAX_SHOW_COUNT = 4;
  @SuppressWarnings("WeakerAccess") @NonNull final PYDroidPreferences preferences;

  AdvertisementInteractor(@NonNull PYDroidPreferences pyDroidPreferences) {
    this.preferences = pyDroidPreferences;
  }

  @NonNull @CheckResult public Offloader<Boolean> showAdView() {
    return AsyncOffloader.newInstance(() -> {
      final boolean isEnabled = preferences.isAdViewEnabled();
      final int shownCount = preferences.getAdViewShownCount();
      final boolean isValidCount = shownCount >= MAX_SHOW_COUNT;

      if (isEnabled && isValidCount) {
        Timber.d("Show ad view");
        return Boolean.TRUE;
      } else {
        Timber.w("Do not show ad view");
        final int newCount = shownCount + 1;
        Timber.d("Increment shown count to %d", newCount);
        preferences.setAdViewShownCount(newCount);
        return Boolean.FALSE;
      }
    });
  }

  @NonNull @CheckResult public Offloader<Boolean> hideAdView() {
    return AsyncOffloader.newInstance(() -> {
      Timber.d("Hide AdView");
      if (preferences.getAdViewShownCount() >= MAX_SHOW_COUNT) {
        Timber.d("Write shown count back to 0");
        preferences.setAdViewShownCount(0);
        return Boolean.TRUE;
      } else {
        return Boolean.FALSE;
      }
    });
  }
}
