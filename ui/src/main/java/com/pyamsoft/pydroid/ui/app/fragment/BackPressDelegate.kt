package com.pyamsoft.pydroid.ui.app.fragment

import androidx.annotation.CheckResult
import androidx.fragment.app.FragmentManager

internal object BackPressDelegate {

  @CheckResult
  internal fun onBackPressed(fragmentManager: FragmentManager): Boolean {
    // Every fragment receives the back event - any fragment can choose to handle it
    // and stop the back press event from calling super
    var handled = false
    val fragments = fragmentManager.fragments
    for (fragment in fragments) {
      if (fragment is BackPressHandler) {
        if (fragment.onBackPressed()) {
          handled = true
        }
      }
    }

    return handled
  }

}
