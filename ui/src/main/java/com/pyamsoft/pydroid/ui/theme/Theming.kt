package com.pyamsoft.pydroid.ui.theme

import android.content.Context
import androidx.annotation.CheckResult
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.pyamsoft.pydroid.ui.R

class Theming internal constructor(context: Context) {

  private val preferences by lazy {
    PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
  }
  private val key by lazy { context.getString(R.string.dark_mode_key) }
  private val defaultValue by lazy { context.resources.getBoolean(R.bool.dark_mode_default) }

  @CheckResult
  fun isDarkTheme(): Boolean {
    return preferences.getBoolean(key, defaultValue)
  }

  fun setDarkTheme(dark: Boolean) {
    preferences.edit {
      putBoolean(key, dark)
    }
  }
}
