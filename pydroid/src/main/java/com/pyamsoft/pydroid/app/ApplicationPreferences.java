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
import timber.log.Timber;

/**
 * This class is intended as a convenient way to interact with single preferences at a time.
 * If you are needing to work with multiple preferences at the same time, stick with the usual
 * Android SharedPreferences implementation
 */
public final class ApplicationPreferences {

  @Nullable private static volatile ApplicationPreferences instance = null;
  @NonNull private final SharedPreferences p;
  private boolean autoCommit;
  @Nullable private volatile SharedPreferences.Editor editor;

  private ApplicationPreferences(@NonNull Context context) {
    Context appContext = context.getApplicationContext();
    p = PreferenceManager.getDefaultSharedPreferences(appContext);
    autoCommit = true;
  }

  /**
   * Retrieve the singleton instance of Application Preferences
   *
   * Guarantee that the singleton is created and non null using double checking synchronization
   */
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

  /**
   * Get the editor as a singleton. Because the same editor object that is edited must also be
   * committed or applied, we return the same editor object each time this is called
   */
  @SuppressLint("CommitPrefEdits") @CheckResult @NonNull
  private SharedPreferences.Editor getEditor() {
    if (editor == null) {
      synchronized (this) {
        if (editor == null) {
          editor = p.edit();
        }
      }
    }

    if (editor == null) {
      throw new IllegalStateException("Editor is NULL");
    } else {
      //noinspection ConstantConditions
      return editor;
    }
  }

  /**
   * Turn on whether or not to auto commit preference changes after any put(), remove(), or clear()
   * function
   */
  public void setAutoCommit(boolean autoCommit) {
    this.autoCommit = autoCommit;
    if (!autoCommit) {
      Timber.w("AutoCommit is NOT enabled, you must call commit manually");
    }
  }

  /**
   * Auto apply the preference changes if autoCommit is set
   */
  private void autoApply() {
    if (autoCommit) {
      apply();
    }
  }

  public void apply() {
    savePreferences(false);
  }

  public void commit() {
    savePreferences(true);
  }

  /**
   * Save the currently edited preferences if the editor is non null.
   * Once committed, we null out the editor so that it can be recreated later.
   */
  private void savePreferences(boolean commit) {
    if (editor == null) {
      throw new IllegalStateException("Editor is NULL");
    } else {
      if (editor != null) {
        synchronized (this) {
          if (editor != null) {
            if (commit) {
              //noinspection ConstantConditions
              editor.commit();
            } else {
              //noinspection ConstantConditions
              editor.apply();
            }
            editor = null;
          }
        }
      }
    }
  }

  @CheckResult @NonNull public ApplicationPreferences put(@NonNull String s, long l) {
    getEditor().putLong(s, l);
    autoApply();
    return this;
  }

  @CheckResult @NonNull public ApplicationPreferences put(@NonNull String s, @NonNull String st) {
    getEditor().putString(s, st);
    autoApply();
    return this;
  }

  @CheckResult @NonNull public ApplicationPreferences put(@NonNull String s, int i) {
    getEditor().putInt(s, i);
    autoApply();
    return this;
  }

  @CheckResult @NonNull public ApplicationPreferences put(@NonNull String s, float f) {
    getEditor().putFloat(s, f);
    autoApply();
    return this;
  }

  @CheckResult @NonNull
  public ApplicationPreferences put(@NonNull String s, @NonNull Set<String> st) {
    getEditor().putStringSet(s, st);
    autoApply();
    return this;
  }

  @CheckResult @NonNull public ApplicationPreferences put(@NonNull String s, boolean b) {
    getEditor().putBoolean(s, b);
    autoApply();
    return this;
  }

  @NonNull @CheckResult public ApplicationPreferences remove(@NonNull String s) {
    getEditor().remove(s);
    autoApply();
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

  @CheckResult @Nullable public final Set<String> get(@NonNull String s, @Nullable Set<String> st) {
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

  public ApplicationPreferences clear() {
    clear(false);
    return this;
  }

  /**
   * We want to guarantee that the preferences are cleared before continuing, so we block on the
   * current thread
   */
  public ApplicationPreferences clear(boolean commit) {
    getEditor().clear();
    if (autoCommit) {
      savePreferences(commit);
    }
    return this;
  }

  public ApplicationPreferences register(
      @NonNull SharedPreferences.OnSharedPreferenceChangeListener l) {
    p.registerOnSharedPreferenceChangeListener(l);
    return this;
  }

  public ApplicationPreferences unregister(
      @NonNull SharedPreferences.OnSharedPreferenceChangeListener l) {
    p.unregisterOnSharedPreferenceChangeListener(l);
    return this;
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
