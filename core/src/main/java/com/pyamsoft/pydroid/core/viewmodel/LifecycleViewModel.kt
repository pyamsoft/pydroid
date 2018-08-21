package com.pyamsoft.pydroid.core.viewmodel

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle.Event.ON_DESTROY
import androidx.lifecycle.Lifecycle.State.INITIALIZED
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

  private data class LifecycleDisposable(
    private val neededForUniqueDataClassHashCodeAndEquals: UUID = UUID.randomUUID()
  ) : LifecycleObserver {

    private var lifecycle: Lifecycle? = null
    private var disposable: Disposable? = null

    internal fun bind(
      disposable: Disposable,
      lifecycle: Lifecycle
    ) {
      val currentState = lifecycle.currentState
      if (currentState.isAtLeast(INITIALIZED)) {
        bindToLifecycle(disposable, lifecycle)
      } else {
        throw IllegalStateException("Lifecycle is invalid state: $currentState")
      }
    }

    private fun bindToLifecycle(
      disposable: Disposable,
      lifecycle: Lifecycle
    ) {
      if (this.disposable != null) {
        throw IllegalStateException("Disposable is already bound!")
      }

      if (this.lifecycle != null) {
        throw IllegalStateException("Lifecycle is already observed!")
      }

      lifecycle.addObserver(this)
      this.lifecycle = lifecycle
      this.disposable = disposable
    }

    @OnLifecycleEvent(ON_DESTROY)
    internal fun onDestroy() {
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
    }
  }
}
