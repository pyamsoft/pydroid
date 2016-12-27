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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.Map;
import java.util.Set;

/**
 * This class is intended as a convenient way to interact with single preferences at a time.
 * If you are needing to work with multiple preferences at the same time, stick with the usual
 * Android SharedPreferences implementation
 */
@SuppressWarnings("unused") public abstract class ApplicationPreferences
    implements SimplePreferences {

  @NonNull private final EditPreferences preferences;

  protected ApplicationPreferences(@NonNull Context context) {
    preferences = new EditPreferences(context);
  }

  @NonNull @Override public EditPreferences put(@NonNull String s, long l) {
    return preferences.put(s, l).apply();
  }

  @NonNull @Override public EditPreferences put(@NonNull String s, @Nullable String st) {
    return preferences.put(s, st).apply();
  }

  @NonNull @Override public EditPreferences put(@NonNull String s, int i) {
    return preferences.put(s, i).apply();
  }

  @NonNull @Override public EditPreferences put(@NonNull String s, float f) {
    return preferences.put(s, f).apply();
  }

  @NonNull @Override public EditPreferences putSet(@NonNull String s, @NonNull Set<String> st) {
    return preferences.putSet(s, st).apply();
  }

  @NonNull @Override public EditPreferences put(@NonNull String s, boolean b) {
    return preferences.put(s, b).apply();
  }

  @Override public long get(@NonNull String s, long l) {
    return preferences.get(s, l);
  }

  @NonNull @Override public String get(@NonNull String s) {
    return preferences.get(s);
  }

  @Override public int get(@NonNull String s, int i) {
    return preferences.get(s, i);
  }

  @Override public float get(@NonNull String s, float f) {
    return preferences.get(s, f);
  }

  @NonNull @Override public Set<String> getSet(@NonNull String s) {
    return preferences.getSet(s);
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

  @NonNull @Override public EditPreferences remove(@NonNull String s) {
    return preferences.remove(s).apply();
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
}
