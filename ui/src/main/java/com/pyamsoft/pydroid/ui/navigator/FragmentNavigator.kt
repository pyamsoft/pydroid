/*
 * Copyright 2021 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.ui.navigator

import android.os.Bundle
import androidx.annotation.CheckResult
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import com.pyamsoft.pydroid.core.Logger
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.ui.util.commit
import com.pyamsoft.pydroid.ui.util.commitNow

/** A navigator backed by AndroidX Fragment transactions */
public abstract class FragmentNavigator<S : Any>
protected constructor(
    private val activity: FragmentActivity,
    @IdRes private val fragmentContainerId: Int,
) : BaseNavigator<S>() {

  private val fragmentTagMap: Map<S, FragmentTag> by
      lazy(LazyThreadSafetyMode.NONE) { provideFragmentTagMap() }

  private val fragmentManager by lazy(LazyThreadSafetyMode.NONE) { activity.supportFragmentManager }

  /** Provides a map of Screen types to FragmentTypes */
  @CheckResult protected abstract fun provideFragmentTagMap(): Map<S, FragmentTag>

  /** Performs a fragment transaction */
  protected abstract fun performFragmentTransaction(
      container: Int,
      data: FragmentTag,
      newScreen: Navigator.Screen<S>,
      previousScreen: S?
  )

  @CheckResult
  private fun getPossibleCurrentFragment(): Fragment? {
    return fragmentManager.findFragmentById(fragmentContainerId)
  }

  final override fun restore(savedInstanceState: Bundle?) {
    if (getPossibleCurrentFragment() == null) {
      if (select(defaultScreen, force = true)) {
        Logger.d("Loaded initial screen: ${defaultScreen.screen}")
      }
    }
  }

  final override fun select(screen: Navigator.Screen<S>, force: Boolean): Boolean {
    val entry = fragmentTagMap[screen.screen].requireNotNull()

    val previousScreen: S?
    val pushNew: Boolean
    val existing = getPossibleCurrentFragment()
    if (existing == null) {
      Logger.d("Pushing a brand new fragment")
      pushNew = true
      previousScreen = null
    } else {
      val tag = existing.tag

      // Look up the previous screen in the map
      previousScreen = fragmentTagMap.entries.find { it.value.tag == tag }?.key

      pushNew =
          if (entry.tag == tag) {
            Logger.d("Pushing the same fragment")
            false
          } else {
            Logger.d("Pushing a new fragment over an old one")
            true
          }
    }

    if (pushNew || force) {
      if (force) {
        Logger.d("Force commit fragment: ${entry.tag}")
      } else {
        Logger.d("Commit fragment: ${entry.tag}")
      }

      updateCurrentScreen(newScreen = screen.screen)
      performFragmentTransaction(
          fragmentContainerId,
          entry,
          screen,
          previousScreen,
      )

      return true
    } else {
      return false
    }
  }

  /** Perform a fragment transaction commit */
  @JvmOverloads
  protected fun commit(
      immediate: Boolean = false,
      transaction: FragmentTransaction.() -> FragmentTransaction,
  ) {
    fragmentManager.commit(
        owner = activity,
        immediate = immediate,
        transaction = transaction,
    )
  }

  /** Perform a fragment transaction commitNow */
  protected fun commitNow(transaction: FragmentTransaction.() -> FragmentTransaction) {
    fragmentManager.commitNow(
        owner = activity,
        transaction = transaction,
    )
  }

  /** A mapping of string Tags to Fragment providers */
  public interface FragmentTag {

    /** Tag */
    public val tag: String

    /** Fragment provider */
    public val fragment: (arguments: Bundle?) -> Fragment
  }
}
