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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.preference.PreferenceManager;
import java.util.Map;
import java.util.Set;

@SuppressLint("CommitPrefEdits") class EditPreferences implements SimplePreferences {

  @NonNull private final SharedPreferences p;

  EditPreferences(@NonNull Context context) {
    Context appContext = context.getApplicationContext();
    this.p = PreferenceManager.getDefaultSharedPreferences(appContext);
  }

  final EditPreferences apply() {
    p.edit().apply();
    return this;
  }

  @NonNull @Override public EditPreferences put(@NonNull String s, long l) {
    p.edit().putLong(s, l);
    return this;
  }

  @NonNull @Override public EditPreferences put(@NonNull String s, @NonNull String st) {
    p.edit().putString(s, st);
    return this;
  }

  @NonNull @Override public EditPreferences put(@NonNull String s, int i) {
    p.edit().putInt(s, i);
    return this;
  }

  @NonNull @Override public EditPreferences put(@NonNull String s, float f) {
    p.edit().putFloat(s, f);
    return this;
  }

  @NonNull @Override public EditPreferences putSet(@NonNull String s, @NonNull Set<String> st) {
    p.edit().putStringSet(s, st);
    return this;
  }

  @NonNull @Override public EditPreferences put(@NonNull String s, boolean b) {
    p.edit().putBoolean(s, b);
    return this;
  }

  @CheckResult @Override public final long get(@NonNull String s, long l) {
    return p.getLong(s, l);
  }

  @Nullable @CheckResult @Override public final String get(@NonNull String s, @Nullable String st) {
    return p.getString(s, st);
  }

  @CheckResult @Override public final int get(@NonNull String s, int i) {
    return p.getInt(s, i);
  }

  @CheckResult @Override public final float get(@NonNull String s, float f) {
    return p.getFloat(s, f);
  }

  @CheckResult @Nullable @Override
  public final Set<String> getSet(@NonNull String s, @Nullable Set<String> st) {
    return p.getStringSet(s, st);
  }

  @CheckResult @Override public final boolean get(@NonNull String s, boolean b) {
    return p.getBoolean(s, b);
  }

  @CheckResult @NonNull @Override public final Map<String, ?> getAll() {
    return p.getAll();
  }

  @CheckResult @Override public final boolean contains(@NonNull String s) {
    return p.contains(s);
  }

  @NonNull @CheckResult @Override public EditPreferences remove(@NonNull String s) {
    p.edit().remove(s);
    return this;
  }

  @Override public final void clear() {
    clear(false);
  }

  /**
   * We want to guarantee that the preferences are cleared before continuing, so we block on the
   * current thread
   */
  @Override public final void clear(boolean commit) {
    final SharedPreferences.Editor editor = p.edit().clear();
    if (commit) {
      editor.commit();
    } else {
      editor.apply();
    }
  }

  @Override
  public final void register(@NonNull SharedPreferences.OnSharedPreferenceChangeListener l) {
    p.registerOnSharedPreferenceChangeListener(l);
  }

  @Override
  public final void unregister(@NonNull SharedPreferences.OnSharedPreferenceChangeListener l) {
    p.unregisterOnSharedPreferenceChangeListener(l);
  }
}
