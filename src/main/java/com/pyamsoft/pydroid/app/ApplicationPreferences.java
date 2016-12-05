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

package com.pyamsoft.pydroid.app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.Map;
import java.util.Set;

/**
 * This class is intended as a convenient way to interact with single preferences at a time.
 * If you are needing to work with multiple preferences at the same time, stick with the usual
 * Android SharedPreferences implementation
 */
@SuppressWarnings("unused") public abstract class ApplicationPreferences {

  @NonNull private final SharedPreferences p;
  @NonNull private final Resources resources;

  protected ApplicationPreferences(final @NonNull Context context) {
    final Context appContext = context.getApplicationContext();
    this.p = PreferenceManager.getDefaultSharedPreferences(appContext);
    resources = appContext.getResources();
  }

  @NonNull @CheckResult protected final Resources getResources() {
    return resources;
  }

  @SuppressWarnings("unused") @NonNull protected final ApplicationPreferences put(@NonNull final String s, final long l) {
    p.edit().putLong(s, l).apply();
    return this;
  }

  @NonNull
  protected final ApplicationPreferences put(@NonNull final String s, @Nullable final String st) {
    p.edit().putString(s, st).apply();
    return this;
  }

  @NonNull protected final ApplicationPreferences put(@NonNull final String s, final int i) {
    p.edit().putInt(s, i).apply();
    return this;
  }

  @SuppressWarnings("unused") @NonNull protected final ApplicationPreferences put(@NonNull final String s, final float f) {
    p.edit().putFloat(s, f).apply();
    return this;
  }

  @SuppressWarnings("unused") @NonNull protected final ApplicationPreferences putSet(@NonNull final String s,
      @NonNull final Set<String> st) {
    p.edit().putStringSet(s, st).apply();
    return this;
  }

  @NonNull protected final ApplicationPreferences put(@NonNull final String s, final boolean b) {
    p.edit().putBoolean(s, b).apply();
    return this;
  }

  @SuppressWarnings("unused") @CheckResult protected final long get(@NonNull final String s, final long l) {
    return p.getLong(s, l);
  }

  @CheckResult protected final String get(@NonNull final String s, final @Nullable String st) {
    return p.getString(s, st);
  }

  @CheckResult protected final int get(@NonNull final String s, final int i) {
    return p.getInt(s, i);
  }

  @CheckResult protected final float get(@NonNull final String s, final float f) {
    return p.getFloat(s, f);
  }

  @SuppressWarnings("unused") @CheckResult @Nullable
  protected final Set<String> getSet(@NonNull final String s, final @Nullable Set<String> st) {
    return p.getStringSet(s, st);
  }

  @CheckResult protected final boolean get(@NonNull final String s, final boolean b) {
    return p.getBoolean(s, b);
  }

  @SuppressWarnings("unused") @CheckResult @NonNull protected final Map<String, ?> getAll() {
    return p.getAll();
  }

  @SuppressWarnings("unused") @CheckResult protected final boolean contains(@NonNull final String s) {
    return p.contains(s);
  }

  @SuppressWarnings("unused") @CheckResult protected final ApplicationPreferences remove(@NonNull final String s) {
    p.edit().remove(s).apply();
    return this;
  }

  @SuppressWarnings("unused") public void clear() {
    clear(false);
  }

  /**
   * We want to guarantee that the preferences are cleared before continuing, so we block on the
   * current thread
   */
  @SuppressLint("CommitPrefEdits") public void clear(final boolean commit) {
    final SharedPreferences.Editor editor = p.edit().clear();
    if (commit) {
      editor.commit();
    } else {
      editor.apply();
    }
  }

  @SuppressWarnings("WeakerAccess")
  public final void register(@NonNull final SharedPreferences.OnSharedPreferenceChangeListener l) {
    p.registerOnSharedPreferenceChangeListener(l);
  }

  @SuppressWarnings("WeakerAccess") public final void unregister(
      @NonNull final SharedPreferences.OnSharedPreferenceChangeListener l) {
    p.unregisterOnSharedPreferenceChangeListener(l);
  }

  public static abstract class OnSharedPreferenceChangeListener
      implements SharedPreferences.OnSharedPreferenceChangeListener {

    boolean isRegistered = false;

    @SuppressWarnings("unused") public final void register(@NonNull final ApplicationPreferences util) {
      if (!isRegistered) {
        util.register(this);
        isRegistered = true;
      }
    }

    @SuppressWarnings("unused") public final void unregister(@NonNull final ApplicationPreferences util) {
      if (isRegistered) {
        util.unregister(this);
        isRegistered = false;
      }
    }
  }
}
