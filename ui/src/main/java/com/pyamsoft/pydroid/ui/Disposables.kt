package com.pyamsoft.pydroid.ui

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

fun Disposable.bind(owner: LifecycleOwner) {
  bind(owner.lifecycle)
}

fun Disposable.bind(lifecycle: Lifecycle) {
  val state = lifecycle.currentState
  val disposable = when {
    state.isAtLeast(RESUMED) -> LifecycleDisposable(disposeOnPause = true)
    state.isAtLeast(STARTED) -> LifecycleDisposable(disposeOnStop = true)
    state.isAtLeast(CREATED) -> LifecycleDisposable(disposeOnDestroy = true)
    else -> throw IllegalStateException("Cannot disposeOnClear Disposable to state: $state")
  }

  disposable.bind(this, lifecycle)
}

private data class LifecycleDisposable internal constructor(
  private val uuid: UUID = UUID.randomUUID(),
  private val disposeOnPause: Boolean = false,
  private val disposeOnStop: Boolean = false,
  private val disposeOnDestroy: Boolean = false
) : LifecycleObserver {

  private var disposable: Disposable? = null
  private var lifecycle: Lifecycle? = null

  fun bind(
    disposable: Disposable,
    lifecycle: Lifecycle
  ) {
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
      lifecycle?.removeObserver(this)
      disposable?.dispose()

      lifecycle = null
      disposable = null
    }
  }

}
