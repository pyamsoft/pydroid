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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import timber.log.Timber;

public abstract class PreferenceBase {

  private final SharedPreferences p;

  protected PreferenceBase(final Context context) {
    final Context appContext = context.getApplicationContext();
    final String preferenceName = appContext.getPackageName() + ".preferences";
    this.p = appContext.getApplicationContext()
        .getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
  }

  protected final PreferenceBase putLong(final String s, final long l) {
    if (p != null) {
      p.edit().putLong(s, l).apply();
    }
    return this;
  }

  protected final PreferenceBase putString(final String s, final String st) {
    if (p != null) {
      p.edit().putString(s, st).apply();
    }
    return this;
  }

  protected final PreferenceBase putInt(final String s, final int i) {
    if (p != null) {
      p.edit().putInt(s, i).apply();
    }
    return this;
  }

  protected final PreferenceBase putFloat(final String s, final float f) {
    if (p != null) {
      p.edit().putFloat(s, f).apply();
    }
    return this;
  }

  protected final PreferenceBase putStringSet(final String s, final Set<String> st) {
    if (p != null) {
      p.edit().putStringSet(s, st).apply();
    }
    return this;
  }

  protected final PreferenceBase putBoolean(final String s, final boolean b) {
    if (p != null) {
      p.edit().putBoolean(s, b).apply();
    }
    return this;
  }

  protected final long getLong(final String s, final long l) {
    long ret = l;
    if (p != null) {
      ret = p.getLong(s, l);
    }
    return ret;
  }

  protected final String getString(final String s, final String st) {
    String ret = st;
    if (p != null) {
      ret = p.getString(s, st);
    }
    return ret;
  }

  protected final int getInt(final String s, final int i) {
    int ret = i;
    if (p != null) {
      ret = p.getInt(s, i);
    }
    return ret;
  }

  protected final float getFloat(final String s, final float f) {
    float ret = f;
    if (p != null) {
      ret = p.getFloat(s, f);
    }
    return ret;
  }

  protected final Set<String> getStringSet(final String s, final Set<String> st) {
    Set<String> ret = st;
    if (p != null) {
      ret = p.getStringSet(s, st);
    }
    return ret;
  }

  protected final boolean getBoolean(final String s, final boolean b) {
    boolean ret = b;
    if (p != null) {
      ret = p.getBoolean(s, b);
    }
    return ret;
  }

  public final boolean register(final SharedPreferences.OnSharedPreferenceChangeListener l) {
    boolean ret = false;
    if (p != null) {
      p.registerOnSharedPreferenceChangeListener(l);
      ret = true;
    }
    return ret;
  }

  protected final Map<String, ?> getAll() {
    Map<String, ?> ret = null;
    if (p != null) {
      ret = p.getAll();
    }
    return ret;
  }

  public final boolean unregister(final SharedPreferences.OnSharedPreferenceChangeListener l) {
    boolean ret = false;
    if (p != null) {
      p.unregisterOnSharedPreferenceChangeListener(l);
      ret = true;
    }
    return ret;
  }

  protected final boolean contains(final String s) {
    boolean ret = false;
    if (p != null) {
      ret = p.contains(s);
    }
    return ret;
  }

  protected final PreferenceBase remove(final String s) {
    if (p != null) {
      p.edit().remove(s).apply();
    }
    return this;
  }

  @SuppressLint("CommitPrefEdits") public void clear() {
    if (p != null) {
      p.edit().clear().commit();
    }
  }

  public static abstract class OnSharedPreferenceChangeListener
      implements SharedPreferences.OnSharedPreferenceChangeListener {

    private final Set<String> keys = new HashSet<>();
    private boolean isRegistered = false;
    private boolean isDebug = false;

    public OnSharedPreferenceChangeListener(final String... keysToListen) {
      if (keysToListen != null) {
        for (final String key : keysToListen) {
          if (key != null) {
            keys.add(key);
          }
        }
      } else {
        throw new RuntimeException("Initializing a new OnSharedPreferenceChangeListener "
            + "that does not watch any keys");
      }
    }

    @Override public final void onSharedPreferenceChanged(final SharedPreferences sharedPreferences,
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

    public final boolean register(final PreferenceBase util) {
      boolean ret = false;
      if (util != null && !isRegistered) {
        ret = util.register(this);
        isRegistered = true;
      }
      return ret;
    }

    public final boolean unregister(final PreferenceBase util) {
      boolean ret = false;
      if (util != null && isRegistered) {
        ret = util.unregister(this);
        isRegistered = false;
      }
      return ret;
    }

    protected abstract void preferenceChanged(final SharedPreferences sharedPreferences,
        final String key);
  }
}
