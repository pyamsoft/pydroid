package com.pyamsoft.pydroid.core.viewmodel

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle.Event.ON_DESTROY
import androidx.lifecycle.Lifecycle.Event.ON_PAUSE
import androidx.lifecycle.Lifecycle.Event.ON_STOP
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
    private val neededForUnique: UUID = UUID.randomUUID()
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
      val state = lifecycle.currentState
      when {
        state.isAtLeast(RESUMED) -> disposeOnPause = true
        state.isAtLeast(STARTED) -> disposeOnStop = true
        else -> disposeOnDestroy = true
      }
      bindToLifecycle(disposable, lifecycle, disposeOnPause, disposeOnStop, disposeOnDestroy)
    }

    internal fun disposeOnClear(
      disposable: Disposable,
      lifecycle: Lifecycle
    ) {
      bindToLifecycle(disposable, lifecycle, disposeOnDestroy = true)
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
        throw IllegalStateException("Must dispose on a lifecycle event!")
      }

      lifecycle.addObserver(this)
      this.lifecycle = lifecycle
      this.disposable = disposable

      this.disposeOnPause = disposeOnPause
      this.disposeOnStop = disposeOnStop
      this.disposeOnDestroy = disposeOnDestroy
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

        this.disposeOnPause = false
        this.disposeOnStop = false
        this.disposeOnDestroy = false
      }
    }
  }
}
