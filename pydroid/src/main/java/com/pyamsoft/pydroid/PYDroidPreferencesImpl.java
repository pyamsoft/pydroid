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

package com.pyamsoft.pydroid;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.v7.preference.PreferenceManager;

final class PYDroidPreferencesImpl implements PYDroidPreferences {

  @NonNull private static final String RATING_ACCEPTED_VERSION = "rating_dialog_accepted_version";
  @NonNull private static final String ADVERTISEMENT_SHOWN_COUNT_KEY = "advertisement_shown_count";
  @NonNull private final String adViewEnabledKey;
  private final boolean adViewEnabledDefault;
  @NonNull private final SharedPreferences preferences;

  PYDroidPreferencesImpl(@NonNull Context context) {
    final Context appContext = context.getApplicationContext();
    final Resources resources = appContext.getResources();
    this.preferences = PreferenceManager.getDefaultSharedPreferences(appContext);

    adViewEnabledKey = appContext.getString(R.string.adview_key);
    adViewEnabledDefault = resources.getBoolean(R.bool.adview_default);
  }

  @CheckResult public boolean isAdViewEnabled() {
    return preferences.getBoolean(adViewEnabledKey, adViewEnabledDefault);
  }

  @Override public int getAdViewShownCount() {
    return preferences.getInt(ADVERTISEMENT_SHOWN_COUNT_KEY, 0);
  }

  @Override public void setAdViewShownCount(int count) {
    preferences.edit().putInt(ADVERTISEMENT_SHOWN_COUNT_KEY, count).apply();
  }

  @Override public int getRatingAcceptedVersion() {
    return preferences.getInt(RATING_ACCEPTED_VERSION, 0);
  }

  @Override public void setRatingAcceptedVersion(int version) {
    preferences.edit().putInt(RATING_ACCEPTED_VERSION, version).apply();
  }
}
