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

import androidx.annotation.CheckResult
import androidx.annotation.IdRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentOnAttachListener
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pydroid.arch.UiSavedStateReader
import com.pyamsoft.pydroid.arch.UiSavedStateWriter
import com.pyamsoft.pydroid.core.Logger
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.ui.util.commit
import com.pyamsoft.pydroid.ui.util.commitNow
import com.pyamsoft.pydroid.util.doOnCreate
import com.pyamsoft.pydroid.util.doOnDestroy

/** A navigator backed by AndroidX Fragment transactions */
public abstract class FragmentNavigator
protected constructor(
    lifecycleOwner: LifecycleOwner,
    fragmentManager: FragmentManager,
    @IdRes private val fragmentContainerId: Int,
) : BaseNavigator<Fragment>(), BackstackNavigator<Fragment> {

  protected constructor(
      activity: FragmentActivity,
      @IdRes fragmentContainerId: Int,
  ) : this(
      lifecycleOwner = activity,
      fragmentManager = activity.supportFragmentManager,
      fragmentContainerId = fragmentContainerId,
  )

  private var lifecycleOwner: LifecycleOwner? = lifecycleOwner
  private var fragmentManager: FragmentManager? = fragmentManager
  private var thisScreen: MutableState<Fragment?>? = mutableStateOf(null)

  init {
    watchBackStack()
    watchFragmentRegistrations()

    // Watch for destroy event
    this.lifecycleOwner?.doOnDestroy {
      Logger.d("Destroy FragmentNavigator on lifecycle destroy")
      this.fragmentManager = null
      this.lifecycleOwner = null
      this.thisScreen = null
    }
  }

  private fun updateCurrentScreenState() {
    thisScreen?.apply {
      val screen = getCurrentExistingFragment()
      Logger.d("Current screen updated")
      this.value = screen
    }
  }

  private fun watchFragmentRegistrations() {
    val listener = FragmentOnAttachListener { _, _ ->
      // Keep the current screen state up to date
      updateCurrentScreenState()
    }

    val fm = this.fragmentManager.requireNotNull()
    val lo = this.lifecycleOwner.requireNotNull()
    lo.doOnCreate { fm.addFragmentOnAttachListener(listener) }
    lo.doOnDestroy { fm.removeFragmentOnAttachListener(listener) }
  }

  private fun watchBackStack() {
    // Keep track of the current stack size at all times
    var stackSize = backStackSize()

    val listener =
        FragmentManager.OnBackStackChangedListener {
          // Keep the current screen state up to date
          updateCurrentScreenState()

          // If the backstack size has been changed, we should update the data backing screen
          val oldSize = stackSize
          val currentSize = backStackSize()

          // Update the stack size (in case the stack has grown)
          stackSize = currentSize

          if (currentSize < oldSize) {
            Logger.d("On Back event!")

            // On back optional callback
            onBack()
          }
        }

    val fm = this.fragmentManager.requireNotNull()
    val lo = this.lifecycleOwner.requireNotNull()
    lo.doOnCreate { fm.addOnBackStackChangedListener(listener) }
    lo.doOnDestroy { fm.removeOnBackStackChangedListener(listener) }
  }

  @CheckResult
  private fun getCurrentExistingFragment(): Fragment? {
    return fragmentManager.requireNotNull().findFragmentById(fragmentContainerId)
  }

  /** Perform a fragment transaction commit */
  @JvmOverloads
  protected fun commit(
      immediate: Boolean = false,
      transaction: FragmentTransaction.() -> FragmentTransaction,
  ) {
    fragmentManager
        .requireNotNull()
        .commit(
            owner = lifecycleOwner.requireNotNull(),
            immediate = immediate,
            transaction = transaction,
        )
  }

  /** Perform a fragment transaction commitNow */
  protected fun commitNow(transaction: FragmentTransaction.() -> FragmentTransaction) {
    fragmentManager
        .requireNotNull()
        .commitNow(
            owner = lifecycleOwner.requireNotNull(),
            transaction = transaction,
        )
  }

  /** Go back immediately based on the FM back stack */
  protected fun goBackNow() {
    fragmentManager.requireNotNull().popBackStackImmediate()
  }

  final override fun loadIfEmpty(onLoadDefaultScreen: () -> Fragment) {
    val existing = getCurrentExistingFragment()
    if (existing == null) {
      Logger.d("No existing Fragment, load default screen")
      val screen = onLoadDefaultScreen()
      navigateTo(screen)
    }
  }

  final override fun goBack() {
    fragmentManager.requireNotNull().popBackStack()
  }

  final override fun backStackSize(): Int {
    return fragmentManager.requireNotNull().backStackEntryCount
  }

  final override fun navigateTo(screen: Fragment, force: Boolean) {
    val existing = getCurrentExistingFragment()

    val pushNew =
        if (existing == null) {
          Logger.d("Pushing a brand new fragment")
          true
        } else {
          if (getTagForFragment(screen) == getTagForFragment(existing)) {
            Logger.d("Pushing the same fragment")
            false
          } else {
            Logger.d("Pushing a new fragment over an old one")
            true
          }
        }

    if (pushNew || force) {
      if (force) {
        Logger.d("Force commit fragment: $screen")
      } else {
        Logger.d("Commit fragment: $screen")
      }

      // Push fragment
      performFragmentTransaction(
          fragmentContainerId,
          screen,
          existing,
      )
    }
  }

  final override fun currentScreen(): Fragment? {
    return thisScreen.requireNotNull().value
  }

  @Composable
  final override fun currentScreenState(): State<Fragment?> {
    return thisScreen.requireNotNull()
  }

  final override fun restoreState(savedInstanceState: UiSavedStateReader) {
    updateCurrentScreenState()
    onRestoreState(savedInstanceState)
  }

  final override fun saveState(outState: UiSavedStateWriter) {
    onSaveState(outState)
  }

  /** Called when [goBack] or [goBackNow] is called */
  protected open fun onBack() {}

  /** Performs a fragment transaction */
  protected abstract fun performFragmentTransaction(
      container: Int,
      newScreen: Fragment,
      previousScreen: Fragment?
  )

  protected abstract fun onRestoreState(savedInstanceState: UiSavedStateReader)

  protected abstract fun onSaveState(outState: UiSavedStateWriter)

  public companion object {

    @JvmStatic
    @CheckResult
    protected fun getTagForFragmentClass(fragment: Class<out Fragment>): String {
      return fragment.name
    }

    @JvmStatic
    @CheckResult
    protected fun getTagForFragment(fragment: Fragment): String {
      return getTagForFragmentClass(fragment::class.java)
    }
  }
}
