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
import com.pyamsoft.pydroid.ui.util.commit
import com.pyamsoft.pydroid.ui.util.commitNow
import com.pyamsoft.pydroid.util.doOnCreate
import com.pyamsoft.pydroid.util.doOnDestroy

/** A navigator backed by AndroidX Fragment transactions */
public abstract class FragmentNavigator<S : Any>
protected constructor(
    lifecycleOwner: LifecycleOwner,
    fragmentManager: FragmentManager,
    @IdRes private val fragmentContainerId: Int,
) : BaseNavigator<S>(), BackstackNavigator<S> {

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
  private var thisScreen: MutableState<S?>? = mutableStateOf(null)

  init {
    watchBackStack()
    watchFragmentRegistrations()

    // Watch for destroy event
    this.lifecycleOwner?.doOnDestroy {
      Logger.d("Destroy FragmentNavigator on lifecycle destroy")

      // Stop any pending messages
      this.handler?.also { it.removeCallbacksAndMessages(null) }

      // Null out
      this.fragmentManager = null
      this.lifecycleOwner = null
      this.thisScreen = null
      this.handler = null
    }
  }

  private fun updateCurrentScreenState() {
    thisScreen?.apply {
      val fragment = getCurrentExistingFragment()
      Logger.d("Current screen updated from fragment: $fragment")
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
  public fun goBackNow() {
    fragmentManager.requireNotNull().popBackStackImmediate()
  }

  final override fun loadIfEmpty(onLoadDefaultScreen: () -> S) {
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

  final override fun navigateTo(screen: S, force: Boolean) {
    val existing = getCurrentExistingFragment()

    val pushNew =
        if (existing == null) {
          Logger.d("Pushing a brand new fragment")
          true
        } else {
          if (screen == getScreenFromFragment(existing)) {
            Logger.d("Pushing the same fragment")
            false
          } else {
            Logger.d("Pushing a new fragment over an old one")
            true
          }
        }

    if (pushNew || force) {

      // Resolve the actual fragment, ensuring that it is also a Screen for future lookups
      val newFragment = produceFragmentForScreen(screen)
      if (newFragment !is Screen<*>) {
        throw IllegalArgumentException("Must implement FragmentNavigator.Screen: $newFragment")
      }

      if (force) {
        Logger.d("Force commit fragment: $screen")
      } else {
        Logger.d("Commit fragment: $screen")
      }

      // Push fragment
      performFragmentTransaction(
          fragmentContainerId,
          newFragment,
          existing,
      )

      // Post an update to the handler since we assume a fragment transaction has taken place, but
      // are unsure
      // if the transaction is commit() or commitNow()
      handler.requireNotNull().post { updateCurrentScreenState() }
    }
  }

  final override fun currentScreen(): S? {
    return thisScreen.requireNotNull().value
  }

  @Composable
  final override fun currentScreenState(): State<S?> {
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
   * The fragment must itself implement the FragmentNavigator.Screen interface
   *
   * We do not want to deal with Fragments directly as screen objects because that will lead to
   * memory leaks since the Navigator holds onto a Fragment for the duration of an Activity scope.
   */
  @CheckResult protected abstract fun produceFragmentForScreen(screen: S): Fragment

  /** Performs a fragment transaction */
  protected abstract fun performFragmentTransaction(
      container: Int,
      newScreen: Fragment,
      previousScreen: Fragment?
  )

  /** Called when state is restored */
  protected abstract fun onRestoreState(savedInstanceState: UiSavedStateReader)

  /** Called when state is saved */
  protected abstract fun onSaveState(outState: UiSavedStateWriter)

  public companion object {

    /** Gets the tag used internally by the Navigator for a given screen instance */
    @JvmStatic
    @CheckResult
    private fun <S : Any> getScreenFromFragment(fragment: Fragment): S {
      if (fragment is Screen<*>) {
        @Suppress("UNCHECKED_CAST") return fragment.getScreenId() as S
      } else {
        throw IllegalArgumentException("Must implement FragmentNavigator.Screen: $fragment")
      }
    }
  }

  /**
   * Screen type for FragmentNavigator
   *
   * Your Fragment must implement this interface to be treated as a valid FragmentNavigator
   * destination
   *
   * Make sure that your implementation is stateless and does not hold a reference to the Fragment
   * itself so that you will not leak memory
   *
   * Example:
   * ```
   * // Any generic typed class as a data holder
   * public interface MyScreen {
   *     public val id: String
   *     public val userName: String
   * }
   *
   * public class ExampleFragment : Fragment(), FragmentNavigator.Screen<MyScreen> {
   *
   *     // Return a stateless object that does not retain a reference to Fragment
   *     override fun getScreenId(): MyScreen {
   *         // You can access fragment specifics here, but the returned object
   *         // should be stateless and not access any fragment data
   *         val id = arguments?.getString("key_id", "").orEmpty()
   *         val userName = arguments?.getString("key_userName", "").orEmpty()
   *
   *          return ExampleFragmentScreen( id = id, userName = userName, )
   *     }
   *
   *     // To use this as a navigate argument, use as follows:
   *     //     navigateTo(ExampleFragmentScreen(id = "1", userName = "test")
   *     public data class ExampleFragmentScreen(
   *         override val id: String,
   *         override val userName: String,
   *     ) : MyScreen
   *
   * }
   * ```
   */
  public fun interface Screen<S : Any> {

    /** Name of the screen */
    @CheckResult public fun getScreenId(): S
  }
}
