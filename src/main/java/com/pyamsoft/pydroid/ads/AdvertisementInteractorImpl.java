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

package com.pyamsoft.pydroid.ads;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.preference.PreferenceManager;
import com.pyamsoft.pydroid.ActionSingle;
import com.pyamsoft.pydroid.R;
import timber.log.Timber;

class AdvertisementInteractorImpl implements AdvertisementInteractor {

  @SuppressWarnings("WeakerAccess") static final int MAX_SHOW_COUNT = 4;
  @SuppressWarnings("WeakerAccess") @NonNull static final String ADVERTISEMENT_SHOWN_COUNT_KEY =
      "advertisement_shown_count";
  @SuppressWarnings("WeakerAccess") @NonNull final String preferenceKey;
  @SuppressWarnings("WeakerAccess") final boolean preferenceDefault;
  @SuppressWarnings("WeakerAccess") @NonNull final SharedPreferences preferences;

  AdvertisementInteractorImpl(@NonNull Context context) {
    final Context appContext = context.getApplicationContext();
    preferences = PreferenceManager.getDefaultSharedPreferences(appContext);
    preferenceKey = appContext.getString(R.string.adview_key);
    preferenceDefault = appContext.getResources().getBoolean(R.bool.adview_default);
  }

  @NonNull @Override
  public AsyncTask<Void, Void, Boolean> showAdView(@NonNull ActionSingle<Boolean> onLoaded) {
    return new AsyncTask<Void, Void, Boolean>() {
      @Override protected Boolean doInBackground(Void... params) {
        final boolean isEnabled = preferences.getBoolean(preferenceKey, preferenceDefault);
        final int shownCount = preferences.getInt(ADVERTISEMENT_SHOWN_COUNT_KEY, 0);
        final boolean isValidCount = shownCount >= MAX_SHOW_COUNT;

        if (isCancelled()) {
          Timber.w("Task cancelled");
          return null;
        } else {
          if (isEnabled && isValidCount) {
            Timber.d("Show ad view");
            return true;
          } else {
            Timber.w("Do not show ad view");
            final int newCount = shownCount + 1;
            Timber.d("Increment shown count to %d", newCount);
            preferences.edit().putInt(ADVERTISEMENT_SHOWN_COUNT_KEY, newCount).apply();
            return false;
          }
        }
      }

      @Override protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        if (result != null) {
          onLoaded.call(result);
        }
      }
    };
  }

  @NonNull @Override
  public AsyncTask<Void, Void, Boolean> hideAdView(@NonNull ActionSingle<Boolean> onLoaded) {
    return new AsyncTask<Void, Void, Boolean>() {
      @Override protected Boolean doInBackground(Void... params) {
        Timber.d("Hide AdView");
        if (isCancelled()) {
          Timber.w("Task cancelled");
          return null;
        } else {
          if (preferences.getInt(ADVERTISEMENT_SHOWN_COUNT_KEY, 0) >= MAX_SHOW_COUNT) {
            Timber.d("Write shown count back to 0");
            preferences.edit().putInt(ADVERTISEMENT_SHOWN_COUNT_KEY, 0).apply();
            return true;
          } else {
            return false;
          }
        }
      }

      @Override protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        if (result != null) {
          onLoaded.call(result);
        }
      }
    };
  }
}
