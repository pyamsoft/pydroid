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
import com.pyamsoft.pydroid.ActionSingle;
import java.util.Map;
import java.util.Set;

/**
 * This class is intended as a convenient way to interact with single preferences at a time.
 * If you are needing to work with multiple preferences at the same time, stick with the usual
 * Android SharedPreferences implementation
 */
public final class ApplicationPreferences {

  @Nullable private static volatile ApplicationPreferences instance = null;
  @NonNull private final SharedPreferences p;

  private ApplicationPreferences(@NonNull Context context) {
    Context appContext = context.getApplicationContext();
    p = PreferenceManager.getDefaultSharedPreferences(appContext);
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

  @NonNull public ApplicationPreferences put(@NonNull String s, long l) {
    p.edit().putLong(s, l).apply();
    return this;
  }

  @NonNull public ApplicationPreferences put(@NonNull String s, @NonNull String st) {
    p.edit().putString(s, st).apply();
    return this;
  }

  @NonNull public ApplicationPreferences put(@NonNull String s, int i) {
    p.edit().putInt(s, i).apply();
    return this;
  }

  @NonNull public ApplicationPreferences put(@NonNull String s, float f) {
    p.edit().putFloat(s, f).apply();
    return this;
  }

  @NonNull public ApplicationPreferences putSet(@NonNull String s, @NonNull Set<String> st) {
    p.edit().putStringSet(s, st).apply();
    return this;
  }

  @NonNull public ApplicationPreferences put(@NonNull String s, boolean b) {
    p.edit().putBoolean(s, b).apply();
    return this;
  }

  @CheckResult public final long get(@NonNull String s, long l) {
    return p.getLong(s, l);
  }

  @Nullable @CheckResult public final String get(@NonNull String s, @Nullable String st) {
    return p.getString(s, st);
  }

  @CheckResult public final int get(@NonNull String s, int i) {
    return p.getInt(s, i);
  }

  @CheckResult public final float get(@NonNull String s, float f) {
    return p.getFloat(s, f);
  }

  @CheckResult @Nullable
  public final Set<String> getSet(@NonNull String s, @Nullable Set<String> st) {
    return p.getStringSet(s, st);
  }

  @CheckResult public final boolean get(@NonNull String s, boolean b) {
    return p.getBoolean(s, b);
  }

  @CheckResult @NonNull public final Map<String, ?> getAll() {
    return p.getAll();
  }

  @CheckResult public final boolean contains(@NonNull String s) {
    return p.contains(s);
  }

  @NonNull @CheckResult public ApplicationPreferences remove(@NonNull String s) {
    p.edit().remove(s).apply();
    return this;
  }

  @CheckResult @NonNull public ApplicationPreferences multiEdit(
      @NonNull ActionSingle<SharedPreferences> preferencesAction) {
    preferencesAction.call(p);
    return this;
  }

  public final void clear() {
    clear(false);
  }

  /**
   * We want to guarantee that the preferences are cleared before continuing, so we block on the
   * current thread
   */
  @SuppressLint("CommitPrefEdits") public final void clear(boolean commit) {
    final SharedPreferences.Editor editor = p.edit().clear();
    if (commit) {
      editor.commit();
    } else {
      editor.apply();
    }
  }

  public final void register(@NonNull SharedPreferences.OnSharedPreferenceChangeListener l) {
    p.registerOnSharedPreferenceChangeListener(l);
  }

  public final void unregister(@NonNull SharedPreferences.OnSharedPreferenceChangeListener l) {
    p.unregisterOnSharedPreferenceChangeListener(l);
  }

  public abstract static class OnSharedPreferenceChangeListener
      implements SharedPreferences.OnSharedPreferenceChangeListener {

    boolean isRegistered = false;

    public void register(@NonNull ApplicationPreferences util) {
      if (!isRegistered) {
        util.register(this);
        isRegistered = true;
      }
    }

    @SuppressWarnings("unused") public void unregister(@NonNull ApplicationPreferences util) {
      if (isRegistered) {
        util.unregister(this);
        isRegistered = false;
      }
    }
  }
}
