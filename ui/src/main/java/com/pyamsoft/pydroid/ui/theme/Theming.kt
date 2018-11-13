package com.pyamsoft.pydroid.ui.theme

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.CheckResult
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.pyamsoft.pydroid.ui.R
import timber.log.Timber

class Theming internal constructor(context: Context) {

  private val preferences: SharedPreferences
  private val key: String

  init {
    val appContext = context.applicationContext
    preferences = PreferenceManager.getDefaultSharedPreferences(appContext)
    key = appContext.getString(R.string.dark_mode_key)

    if (!preferences.contains(key)) {
      setDarkTheme(IS_DEFAULT_DARK_THEME)
    }
  }

  @CheckResult
  fun isDarkTheme(): Boolean {
    return preferences.getBoolean(key, IS_DEFAULT_DARK_THEME)
  }

  fun setDarkTheme(dark: Boolean) {
    preferences.edit {
      Timber.d("Set dark theme: $dark")
      putBoolean(key, dark)
    }
  }

  companion object {

    @JvmField
    var IS_DEFAULT_DARK_THEME = false
  }
}
