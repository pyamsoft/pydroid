package com.pyamsoft.pydroid.core.viewmodel

import androidx.annotation.CheckResult
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle.Event.ON_DESTROY
import androidx.lifecycle.Lifecycle.Event.ON_PAUSE
import androidx.lifecycle.Lifecycle.Event.ON_STOP
import androidx.lifecycle.Lifecycle.State.CREATED
import androidx.lifecycle.Lifecycle.State.RESUMED
import androidx.lifecycle.Lifecycle.State.STARTED
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import io.reactivex.disposables.Disposable
import java.util.UUID

interface LifecycleViewModel {

  fun Disposable.bind(owner: LifecycleOwner) {
    bind(owner.lifecycle)
  }

  fun Disposable.bind(lifecycle: Lifecycle) {
    LifecycleDisposable().bind(this, lifecycle)
  }

  fun Disposable.disposeOnClear(owner: LifecycleOwner) {
    disposeOnClear(owner.lifecycle)
  }

  fun Disposable.disposeOnClear(lifecycle: Lifecycle) {
    LifecycleDisposable().disposeOnClear(this, lifecycle)
  }

  private data class LifecycleDisposable(
    private val neededForUniqueDataClassHashCodeAndEquals: UUID = UUID.randomUUID()
  ) : LifecycleObserver {

    private var lifecycle: Lifecycle? = null
    private var disposable: Disposable? = null

    private var disposeOnPause: Boolean = false
    private var disposeOnStop: Boolean = false
    private var disposeOnDestroy: Boolean = false

    internal fun bind(
      disposable: Disposable,
      lifecycle: Lifecycle
    ) {
      var disposeOnPause = false
      var disposeOnStop = false
      var disposeOnDestroy = false
      val currentState = lifecycle.currentState
      when {
        currentState.isAtLeast(RESUMED) -> disposeOnPause = true
        currentState.isAtLeast(STARTED) -> disposeOnStop = true
        currentState.isAtLeast(CREATED) -> disposeOnDestroy = true
        else -> throw IllegalStateException("Lifecycle is invalid state: $currentState")
      }

      bindToLifecycle(disposable, lifecycle, disposeOnPause, disposeOnStop, disposeOnDestroy)
    }

    internal fun disposeOnClear(
      disposable: Disposable,
      lifecycle: Lifecycle
    ) {
      val currentState = lifecycle.currentState
      if (currentState.isAtLeast(CREATED)) {
        bindToLifecycle(disposable, lifecycle, disposeOnDestroy = true)
      } else {
        throw IllegalStateException("Lifecycle is invalid state: $currentState")
      }
    }

    private fun bindToLifecycle(
      disposable: Disposable,
      lifecycle: Lifecycle,
      disposeOnPause: Boolean = false,
      disposeOnStop: Boolean = false,
      disposeOnDestroy: Boolean = false
    ) {
      if (this.disposable != null) {
        throw IllegalStateException("Disposable is already bound!")
      }

      if (this.lifecycle != null) {
        throw IllegalStateException("Lifecycle is already observed!")
      }

      if (!disposeOnPause && !disposeOnStop && !disposeOnDestroy) {
        throw IllegalStateException("Cannot disposeOnClear Disposable - will never be disposed!")
      }

      lifecycle.addObserver(this)
      this.lifecycle = lifecycle
      this.disposable = disposable
    }

    @OnLifecycleEvent(ON_PAUSE)
    internal fun onPause() {
      disposeOnCondition(disposeOnPause)
    }

    @OnLifecycleEvent(ON_STOP)
    internal fun onStop() {
      disposeOnCondition(disposeOnStop)
    }

    @OnLifecycleEvent(ON_DESTROY)
    internal fun onDestroy() {
      disposeOnCondition(disposeOnDestroy)
    }

    private fun disposeOnCondition(condition: Boolean) {
      if (condition) {
        if (disposable == null) {
          throw IllegalStateException("Disposable is already disposed!")
        }

        if (lifecycle == null) {
          throw IllegalStateException("Lifecycle is already unbound!")
        }

        disposable?.also {
          if (!it.isDisposed) {
            it.dispose()
          }
        }
        lifecycle?.removeObserver(this)

        disposable = null
        lifecycle = null
        disposeOnPause = false
        disposeOnStop = false
        disposeOnDestroy = false
      }
    }

  }

  companion object {

    // A simple viewBus interface for view model local events
    @CheckResult
    fun <T : Any> viewBus(): ViewModelBus<T> {
      return ViewModelBusImpl()
    }
  }

}
