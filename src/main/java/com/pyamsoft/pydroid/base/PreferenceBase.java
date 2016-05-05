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

package com.pyamsoft.pydroid.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import timber.log.Timber;

public abstract class PreferenceBase {

  @NonNull private final SharedPreferences p;
  private boolean strict;

  protected PreferenceBase(final @NonNull Context context) {
    this(context.getApplicationContext(), true);
  }

  protected PreferenceBase(final @NonNull Context context, final boolean strict) {
    final Context appContext = context.getApplicationContext();
    final String preferenceName = appContext.getPackageName() + ".preferences";
    this.p = appContext.getApplicationContext()
        .getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
    this.strict = strict;
  }

  private void offMainThread() {
    if (Thread.currentThread() == Looper.getMainLooper().getThread() && strict) {
      Timber.e("Call is running on same thread as MainLooper");
      throw new MainThreadAccessException();
    }
  }

  @SuppressLint("CommitPrefEdits") @WorkerThread
  protected final PreferenceBase put(@NonNull final String s, final long l) {
    offMainThread();
    p.edit().putLong(s, l).commit();
    return this;
  }

  @SuppressLint("CommitPrefEdits") @WorkerThread
  protected final PreferenceBase put(@NonNull final String s, @Nullable final String st) {
    offMainThread();
    p.edit().putString(s, st).commit();
    return this;
  }

  @SuppressLint("CommitPrefEdits") @WorkerThread
  protected final PreferenceBase put(@NonNull final String s, final int i) {
    offMainThread();
    p.edit().putInt(s, i).commit();
    return this;
  }

  @SuppressLint("CommitPrefEdits") @WorkerThread
  protected final PreferenceBase put(@NonNull final String s, final float f) {
    offMainThread();
    p.edit().putFloat(s, f).commit();
    return this;
  }

  @SuppressLint("CommitPrefEdits") @WorkerThread
  protected final PreferenceBase putSet(@NonNull final String s, @NonNull final Set<String> st) {
    offMainThread();
    p.edit().putStringSet(s, st).commit();
    return this;
  }

  @SuppressLint("CommitPrefEdits") @WorkerThread
  protected final PreferenceBase put(@NonNull final String s, final boolean b) {
    offMainThread();
    p.edit().putBoolean(s, b).commit();
    return this;
  }

  @WorkerThread protected final long get(@NonNull final String s, final long l) {
    offMainThread();
    return p.getLong(s, l);
  }

  @WorkerThread protected final String get(@NonNull final String s, final @Nullable String st) {
    offMainThread();
    return p.getString(s, st);
  }

  @WorkerThread protected final int get(@NonNull final String s, final int i) {
    offMainThread();
    return p.getInt(s, i);
  }

  @WorkerThread protected final float get(@NonNull final String s, final float f) {
    offMainThread();
    return p.getFloat(s, f);
  }

  @WorkerThread
  protected final Set<String> getSet(@NonNull final String s, final @Nullable Set<String> st) {
    offMainThread();
    return p.getStringSet(s, st);
  }

  @WorkerThread protected final boolean get(@NonNull final String s, final boolean b) {
    offMainThread();
    return p.getBoolean(s, b);
  }

  @WorkerThread protected final Map<String, ?> getAll() {
    offMainThread();
    return p.getAll();
  }

  @WorkerThread protected final boolean contains(@NonNull final String s) {
    offMainThread();
    return p.contains(s);
  }

  @SuppressLint("CommitPrefEdits") @WorkerThread
  protected final PreferenceBase remove(@NonNull final String s) {
    offMainThread();
    p.edit().remove(s).commit();
    return this;
  }

  @WorkerThread @SuppressLint("CommitPrefEdits") public void clear() {
    offMainThread();
    p.edit().clear().commit();
  }

  @WorkerThread
  public final void register(@NonNull final SharedPreferences.OnSharedPreferenceChangeListener l) {
    p.registerOnSharedPreferenceChangeListener(l);
  }

  @WorkerThread public final void unregister(
      @NonNull final SharedPreferences.OnSharedPreferenceChangeListener l) {
    p.unregisterOnSharedPreferenceChangeListener(l);
  }

  public static abstract class OnSharedPreferenceChangeListener
      implements SharedPreferences.OnSharedPreferenceChangeListener {

    private final Set<String> keys = new HashSet<>();
    private boolean isRegistered = false;
    private boolean isDebug = false;

    public OnSharedPreferenceChangeListener(@NonNull final String... keysToListen) {
      for (final String key : keysToListen) {
        if (key != null) {
          keys.add(key);
        }
      }
    }

    @Override
    public final void onSharedPreferenceChanged(@NonNull final SharedPreferences sharedPreferences,
        final String key) {
      if (keys.contains(key)) {
        preferenceChanged(sharedPreferences, key);
      } else {
        if (isDebug) {
          Timber.d("Key: %s not in key set", key);
        }
      }
    }

    public final OnSharedPreferenceChangeListener setDebug(final boolean debug) {
      isDebug = debug;
      return this;
    }

    public final void register(@NonNull final PreferenceBase util) {
      if (!isRegistered) {
        util.register(this);
        isRegistered = true;
      }
    }

    public final void unregister(@NonNull final PreferenceBase util) {
      if (isRegistered) {
        util.unregister(this);
        isRegistered = false;
      }
    }

    protected abstract void preferenceChanged(@NonNull final SharedPreferences sharedPreferences,
        @NonNull final String key);
  }

  static final class MainThreadAccessException extends RuntimeException {

    public MainThreadAccessException() {
      this("Cannot access SharedPreferences on Main Thread");
    }

    public MainThreadAccessException(@NonNull String detailMessage) {
      super(detailMessage);
    }
  }
}
