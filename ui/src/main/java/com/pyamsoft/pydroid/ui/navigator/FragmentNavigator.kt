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

import android.os.Handler
import android.os.Looper
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
import com.pyamsoft.pydroid.ui.navigator.FragmentNavigator.Screen
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
) : BaseNavigator<Screen>(), BackstackNavigator<Screen> {

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
  private var handler: Handler? = Handler(Looper.getMainLooper())

  private var thisScreen: MutableState<Screen?>? = mutableStateOf(null)

  init {
    watchBackStack()
    watchFragmentRegistrations()

    // Watch for destroy event
    this.lifecycleOwner?.doOnDestroy {
      Logger.d("Destroy FragmentNavigator on lifecycle destroy")
      this.fragmentManager = null
      this.lifecycleOwner = null
      this.thisScreen = null
      this.handler?.also { it.removeCallbacksAndMessages(null) }
      this.handler = null
    }
  }

  private fun updateCurrentScreenState() {
    thisScreen?.apply {
      val fragment = getCurrentExistingFragment()
      Logger.d("Current screen updated")
      this.value = if (fragment == null) null else getScreenFromFragment(fragment)
    }
  }

  private fun watchFragmentRegistrations() {
    val listener = FragmentOnAttachListener { _, _ ->
      // Keep the current screen state up to date
      Logger.d("New fragment attached")
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
          Logger.d("Back stack size changed")
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

    // Post an update to the handler
    handler.requireNotNull().post { updateCurrentScreenState() }
  }

  /** Perform a fragment transaction commitNow */
  protected fun commitNow(transaction: FragmentTransaction.() -> FragmentTransaction) {
    fragmentManager
        .requireNotNull()
        .commitNow(
            owner = lifecycleOwner.requireNotNull(),
            transaction = transaction,
        )

    // Post an update to the handler
    handler.requireNotNull().post { updateCurrentScreenState() }
  }

  /** Go back immediately based on the FM back stack */
  protected fun goBackNow() {
    fragmentManager.requireNotNull().popBackStackImmediate()
  }

  final override fun loadIfEmpty(onLoadDefaultScreen: () -> Screen) {
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

  final override fun navigateTo(screen: Screen, force: Boolean) {
    val existing = getCurrentExistingFragment()

    val pushNew =
        if (existing == null) {
          Logger.d("Pushing a brand new fragment")
          true
        } else {
          if (screen matches getScreenFromFragment(existing)) {
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
          produceFragmentForScreen(screen),
          existing,
      )

      // Post an update to the handler since we assume a fragment transaction has taken place, but
      // are unsure
      // if the transaction is commit() or commitNow()
      handler.requireNotNull().post { updateCurrentScreenState() }
    }
  }

  final override fun currentScreen(): Screen? {
    return thisScreen.requireNotNull().value
  }

  @Composable
  final override fun currentScreenState(): State<Screen?> {
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

  /**
   * Given a Screen here generate a new Fragment
   *
   * We do not want to deal with Fragments directly as screen objects because that will lead to
   * memory leaks since the Navigator holds onto a Fragment for the duration of an Activity scope.
   */
  @CheckResult
  protected abstract fun <R> produceFragmentForScreen(screen: Screen): R where
  R : Fragment,
  R : Screen

  /** Performs a fragment transaction */
  protected abstract fun <F> performFragmentTransaction(
      container: Int,
      newScreen: F,
      previousScreen: F?
  ) where F : Fragment, F : Screen

  /** Called when state is restored */
  protected abstract fun onRestoreState(savedInstanceState: UiSavedStateReader)

  /** Called when state is saved */
  protected abstract fun onSaveState(outState: UiSavedStateWriter)

  public companion object {

    /** Gets the tag used internally by the Navigator for a given screen instance */
    @JvmStatic
    @CheckResult
    private fun getScreenFromFragment(fragment: Fragment): Screen {
      if (fragment is Screen) {
        return fragment
      } else {
        throw IllegalArgumentException("Must implement FragmentNavigator.Screen: $fragment")
      }
    }
  }

  /** Screen type for FragmentNavigator */
  public fun interface Screen {

    /** Name of the screen */
    @CheckResult public fun getScreenName(): String
  }

  /**
   * Do these screens match
   *
   * Use this instead of using screen == otherScreen
   */
  @CheckResult
  public infix fun Screen.matches(screen: Screen): Boolean {
    return this.getScreenName() == screen.getScreenName()
  }
}
