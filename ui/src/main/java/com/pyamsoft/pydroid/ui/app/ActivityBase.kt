/*
 * Copyright 2019 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.ui.app

import androidx.annotation.CallSuper
import androidx.annotation.CheckResult
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentManager
import com.pyamsoft.pydroid.util.runWhenReady
import timber.log.Timber

abstract class ActivityBase : AppCompatActivity(), ToolbarActivity, ToolbarActivityProvider {

  /**
   * Whether back press should respect fragment backstack state
   */
  protected var respectFragmentBackStack: Boolean = true

  /**
   * Whether back press should respect fragment child backstack state
   */
  protected var respectChildFragmentBackStack: Boolean = true

  /**
   * The main view container for all page level fragment transactions
   */
  abstract val fragmentContainerId: Int

  private var capturedToolbar: Toolbar? = null

  @CallSuper
  override fun onDestroy() {
    super.onDestroy()

    // Clear captured Toolbar
    capturedToolbar = null
  }

  final override fun withToolbar(func: (Toolbar) -> Unit) {
    capturedToolbar?.let(func)
  }

  final override fun requireToolbar(func: (Toolbar) -> Unit) {
    requireNotNull(capturedToolbar).let(func)
  }

  final override fun setToolbar(toolbar: Toolbar?) {
    capturedToolbar = toolbar
  }

  override fun onBackPressed() {
    var handled = false
    if (respectFragmentBackStack || respectChildFragmentBackStack) {
      val fragments = supportFragmentManager.fragments
      for (fragment in fragments.filterNot { it == null }.reversed()) {
        if (respectChildFragmentBackStack) {
          if (popFragmentManager(fragment.childFragmentManager, fragment.tag)) {
            handled = true
            break
          }
        }

        if (respectFragmentBackStack) {
          if (popFragmentManager(fragment.requireFragmentManager(), fragment.tag)) {
            handled = true
            break
          }
        }
      }
    }

    if (!handled) {
      super.onBackPressed()
    }
  }

  @CheckResult
  private fun popFragmentManager(
    fragmentManager: FragmentManager,
    tag: String?
  ): Boolean {
    if (fragmentManager.backStackEntryCount > 0) {
      runWhenReady(this) {
        Timber.d("Pop backstack: $tag")
        fragmentManager.popBackStackImmediate()
      }

      return true
    }

    return false
  }
}
