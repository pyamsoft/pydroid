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

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.pyamsoft.pydroid.ActionSingle;
import java.util.Map;
import java.util.Set;

/**
 * This class is intended as a convenient way to interact with single preferences at a time.
 * If you are needing to work with multiple preferences at the same time, stick with the usual
 * Android SharedPreferences implementation
 */
@SuppressWarnings("unused") public class ApplicationPreferences
    implements SimplePreferences, MultiEditPreference<ApplicationPreferences> {

  @Nullable private static volatile ApplicationPreferences instance = null;
  @NonNull private final EditPreferences preferences;

  private ApplicationPreferences(@NonNull Context context) {
    preferences = new EditPreferences(context);
  }

  @CheckResult @NonNull public static ApplicationPreferences getInstance(@NonNull Context context) {
    //noinspection ConstantConditions
    if (context == null) {
      throw new IllegalArgumentException("Context is NULL");
    }

    if (instance == null) {
      synchronized (ApplicationPreferences.class) {
        if (instance == null) {
          instance = new ApplicationPreferences(context.getApplicationContext());
        }
      }
    }

    //noinspection ConstantConditions
    return instance;
  }

  @NonNull @Override public ApplicationPreferences put(@NonNull String s, long l) {
    preferences.put(s, l).apply();
    return this;
  }

  @NonNull @Override public ApplicationPreferences put(@NonNull String s, @NonNull String st) {
    preferences.put(s, st).apply();
    return this;
  }

  @NonNull @Override public ApplicationPreferences put(@NonNull String s, int i) {
    preferences.put(s, i).apply();
    return this;
  }

  @NonNull @Override public ApplicationPreferences put(@NonNull String s, float f) {
    preferences.put(s, f).apply();
    return this;
  }

  @NonNull @Override
  public ApplicationPreferences putSet(@NonNull String s, @NonNull Set<String> st) {
    preferences.putSet(s, st).apply();
    return this;
  }

  @NonNull @Override public ApplicationPreferences put(@NonNull String s, boolean b) {
    preferences.put(s, b).apply();
    return this;
  }

  @Override public long get(@NonNull String s, long l) {
    return preferences.get(s, l);
  }

  @Nullable @Override public String get(@NonNull String s, @Nullable String st) {
    return preferences.get(s, st);
  }

  @Override public int get(@NonNull String s, int i) {
    return preferences.get(s, i);
  }

  @Override public float get(@NonNull String s, float f) {
    return preferences.get(s, f);
  }

  @Nullable @Override public Set<String> getSet(@NonNull String s, @Nullable Set<String> st) {
    return preferences.getSet(s, st);
  }

  @Override public boolean get(@NonNull String s, boolean b) {
    return preferences.get(s, b);
  }

  @NonNull @Override public Map<String, ?> getAll() {
    return preferences.getAll();
  }

  @Override public boolean contains(@NonNull String s) {
    return preferences.contains(s);
  }

  @NonNull @Override public ApplicationPreferences remove(@NonNull String s) {
    preferences.remove(s).apply();
    return this;
  }

  @Override public void clear() {
    preferences.clear();
  }

  @Override public void clear(boolean commit) {
    preferences.clear(commit);
  }

  @Override public void register(@NonNull SharedPreferences.OnSharedPreferenceChangeListener l) {
    preferences.register(l);
  }

  @Override public void unregister(@NonNull SharedPreferences.OnSharedPreferenceChangeListener l) {
    preferences.unregister(l);
  }

  @NonNull @Override
  public ApplicationPreferences multiEdit(@NonNull ActionSingle<SimplePreferences> call) {
    call.call(preferences);
    preferences.apply();
    return this;
  }
}
