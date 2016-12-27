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

package com.pyamsoft.pydroid.app;

import android.content.SharedPreferences;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.Map;
import java.util.Set;

interface SimplePreferences {

  @CheckResult @NonNull SimplePreferences put(@NonNull String s, long l);

  @CheckResult @NonNull SimplePreferences put(@NonNull String s, @Nullable String st);

  @CheckResult @NonNull SimplePreferences put(@NonNull String s, int i);

  @CheckResult @NonNull SimplePreferences put(@NonNull String s, float f);

  @CheckResult @NonNull SimplePreferences putSet(@NonNull String s, @NonNull Set<String> st);

  @CheckResult @NonNull SimplePreferences put(@NonNull String s, boolean b);

  @CheckResult long get(@NonNull String s, long l);

  @NonNull @CheckResult String get(@NonNull String s);

  @CheckResult int get(@NonNull String s, int i);

  @CheckResult float get(@NonNull String s, float f);

  @CheckResult @NonNull Set<String> getSet(@NonNull String s);

  @CheckResult boolean get(@NonNull String s, boolean b);

  @CheckResult @NonNull Map<String, ?> getAll();

  @CheckResult boolean contains(@NonNull String s);

  @NonNull @CheckResult SimplePreferences remove(@NonNull String s);

  void clear();

  void clear(boolean commit);

  void register(@NonNull SharedPreferences.OnSharedPreferenceChangeListener l);

  void unregister(@NonNull SharedPreferences.OnSharedPreferenceChangeListener l);

  abstract class OnSharedPreferenceChangeListener
      implements SharedPreferences.OnSharedPreferenceChangeListener {

    boolean isRegistered = false;

    public void register(@NonNull SimplePreferences util) {
      if (!isRegistered) {
        util.register(this);
        isRegistered = true;
      }
    }

    @SuppressWarnings("unused") public void unregister(@NonNull SimplePreferences util) {
      if (isRegistered) {
        util.unregister(this);
        isRegistered = false;
      }
    }
  }
}
