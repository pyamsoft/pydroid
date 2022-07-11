/*
 * Copyright 2022 Peter Kenji Yamanaka
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
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pydroid.core.Logger
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.ui.util.commit
import com.pyamsoft.pydroid.ui.util.commitNow
import com.pyamsoft.pydroid.util.doOnCreate
import com.pyamsoft.pydroid.util.doOnDestroy

/** A navigator backed by AndroidX Fragment transactions */
public abstract class FragmentNavigator<S : Any>
protected constructor(
    lifecycleOwner: () -> LifecycleOwner,
    fragmentManager: () -> FragmentManager,
    @IdRes private val fragmentContainerId: Int,
) : BaseNavigator<S>(), BackstackNavigator<S> {

  protected constructor(
      activity: FragmentActivity,
      @IdRes fragmentContainerId: Int,
  ) : this(
      lifecycleOwner = { activity },
      fragmentManager = { activity.supportFragmentManager },
      fragmentContainerId = fragmentContainerId,
  )

  private val lifecycleOwner by lazy(LazyThreadSafetyMode.NONE) { lifecycleOwner() }
  private val fragmentManager by lazy(LazyThreadSafetyMode.NONE) { fragmentManager() }

  private val fragmentTagMap: Map<S, FragmentTag> by
      lazy(LazyThreadSafetyMode.NONE) { provideFragmentTagMap() }

  init {
    // Keep track of the current stack size at all times
    var stackSize = backStackSize()

    val listener =
        FragmentManager.OnBackStackChangedListener {
          // If the backstack size has been changed, we should update the data backing screen
          val oldSize = stackSize
          val currentSize = backStackSize()

          // Update the stack size (in case the stack has grown)
          stackSize = currentSize

          if (currentSize < oldSize) {
            Logger.d("On Back event!")

            // Because the data backing screen has not been updated yet, we can pull the "current"
            // screen from data backing as the previous screen
            val previousScreen = currentScreen()

            // Update current screen data backing now
            val currentScreen = getCurrentScreenFromFragment()
            if (currentScreen == null) {
              clearCurrentScreen()
            } else {
              updateCurrentScreen(newScreen = currentScreen)
            }

            // On back optional callback
            onBack(currentScreen, previousScreen)
          }
        }

    val fm = this.fragmentManager
    val lo = this.lifecycleOwner
    lo.doOnCreate { fm.addOnBackStackChangedListener(listener) }
    lo.doOnDestroy { fm.removeOnBackStackChangedListener(listener) }
  }

  @CheckResult
  private fun getCurrentExistingFragment(): Fragment? {
    return fragmentManager.findFragmentById(fragmentContainerId)
  }

  private fun handleBackStackPopped(previousScreen: S?) {
    // The back stack is updated, grab the current screen which was the "previous" screen and
    // update the screen data
    val currentScreen = getCurrentScreenFromFragment()

    Logger.d("Screen restored to: $currentScreen from $previousScreen")
    onBack(currentScreen, previousScreen)
  }

  @CheckResult
  private fun getCurrentScreenFromFragment(): S? {
    val existing = getCurrentExistingFragment() ?: return null

    // Look up the previous screen in the map
    val tag = existing.tag
    return fragmentTagMap.entries.find { it.value.tag == tag }?.key
  }

  /** Perform a fragment transaction commit */
  @JvmOverloads
  protected fun commit(
      immediate: Boolean = false,
      transaction: FragmentTransaction.() -> FragmentTransaction,
  ) {
    fragmentManager.commit(
        owner = lifecycleOwner,
        immediate = immediate,
        transaction = transaction,
    )
  }

  /** Perform a fragment transaction commitNow */
  protected fun commitNow(transaction: FragmentTransaction.() -> FragmentTransaction) {
    fragmentManager.commitNow(
        owner = lifecycleOwner,
        transaction = transaction,
    )
  }

  /** Go back immediately based on the FM back stack */
  protected fun goBackNow() {
    // Grab current screen before pop
    val screen = currentScreen()

    fragmentManager.popBackStackImmediate()

    // Since this happens immediately, we can avoid handler.post {}
    handleBackStackPopped(screen)
  }

  final override fun loadIfEmpty(onLoadDefaultScreen: () -> Navigator.Screen<S>) {
    val existing = getCurrentExistingFragment()
    if (existing == null) {
      Logger.d("No existing Fragment, load default screen")
      val screen = onLoadDefaultScreen()
      navigateTo(screen)
    }
  }

  final override fun goBack() {
    Logger.d("Go Back!")
    fragmentManager.popBackStack()
  }

  final override fun backStackSize(): Int {
    return fragmentManager.backStackEntryCount
  }

  final override fun navigateTo(screen: Navigator.Screen<S>, force: Boolean) {
    val entry = fragmentTagMap[screen.screen].requireNotNull()

    val existing = getCurrentExistingFragment()

    val pushNew =
        if (existing == null) {
          Logger.d("Pushing a brand new fragment")
          true
        } else {
          val tag = existing.tag

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

      // Grab screen data before updating
      val previousScreen = getCurrentScreenFromFragment()

      // Push fragment
      performFragmentTransaction(
          fragmentContainerId,
          entry,
          screen,
          previousScreen,
      )

      // After we post the fragment, we update the screen data
      //
      // Because of fragment quirks with commit versus commitNow, the fragment
      // may not actually be visually displayed at this point
      updateCurrentScreen(newScreen = screen.screen)
    }
  }

  /** Called when [goBack] or [goBackNow] is called */
  protected open fun onBack(
      currentScreen: S?,
      previousScreen: S?,
  ) {}

  /** Provides a map of Screen types to FragmentTypes */
  @CheckResult protected abstract fun provideFragmentTagMap(): Map<S, FragmentTag>

  /** Performs a fragment transaction */
  protected abstract fun performFragmentTransaction(
      container: Int,
      data: FragmentTag,
      newScreen: Navigator.Screen<S>,
      previousScreen: S?
  )

  /** A mapping of string Tags to Fragment providers */
  public interface FragmentTag {

    /** Tag */
    public val tag: String

    /** Fragment provider */
    public val fragment: (arguments: Bundle?) -> Fragment
  }
}
