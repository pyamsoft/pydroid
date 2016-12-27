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

import android.content.Context;
import android.support.annotation.NonNull;
import com.pyamsoft.pydroid.R;
import com.pyamsoft.pydroid.app.ApplicationPreferences;
import com.pyamsoft.pydroid.tool.AsyncOffloader;
import com.pyamsoft.pydroid.tool.Offloader;
import timber.log.Timber;

class AdvertisementInteractorImpl implements AdvertisementInteractor {

  @SuppressWarnings("WeakerAccess") static final int MAX_SHOW_COUNT = 4;
  @SuppressWarnings("WeakerAccess") @NonNull static final String ADVERTISEMENT_SHOWN_COUNT_KEY =
      "advertisement_shown_count";
  @SuppressWarnings("WeakerAccess") @NonNull final String preferenceKey;
  @SuppressWarnings("WeakerAccess") final boolean preferenceDefault;
  @SuppressWarnings("WeakerAccess") @NonNull final ApplicationPreferences preferences;

  AdvertisementInteractorImpl(@NonNull Context context) {
    final Context appContext = context.getApplicationContext();
    preferences = ApplicationPreferences.getInstance(context);
    preferenceKey = appContext.getString(R.string.adview_key);
    preferenceDefault = appContext.getResources().getBoolean(R.bool.adview_default);
  }

  @NonNull @Override public Offloader<Boolean> showAdView() {
    return AsyncOffloader.newInstance(() -> {
      final boolean isEnabled = preferences.get(preferenceKey, preferenceDefault);
      final int shownCount = preferences.get(ADVERTISEMENT_SHOWN_COUNT_KEY, 0);
      final boolean isValidCount = shownCount >= MAX_SHOW_COUNT;

      if (isEnabled && isValidCount) {
        Timber.d("Show ad view");
        return Boolean.TRUE;
      } else {
        Timber.w("Do not show ad view");
        final int newCount = shownCount + 1;
        Timber.d("Increment shown count to %d", newCount);
        preferences.put(ADVERTISEMENT_SHOWN_COUNT_KEY, newCount);
        return Boolean.FALSE;
      }
    });
  }

  @NonNull @Override public Offloader<Boolean> hideAdView() {
    return AsyncOffloader.newInstance(() -> {
      Timber.d("Hide AdView");
      if (preferences.get(ADVERTISEMENT_SHOWN_COUNT_KEY, 0) >= MAX_SHOW_COUNT) {
        Timber.d("Write shown count back to 0");
        preferences.put(ADVERTISEMENT_SHOWN_COUNT_KEY, 0);
        return Boolean.TRUE;
      } else {
        return Boolean.FALSE;
      }
    });
  }
}
