package com.pyamsoft.pydroid.core.viewmodel

import androidx.lifecycle.Lifecycle.Event.ON_DESTROY
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BaseViewModel(private val owner: LifecycleOwner) : LifecycleObserver {

  private val compositeDisposable = CompositeDisposable()

  init {
    bindToLifecycle()
  }

  private fun bindToLifecycle() {
    owner.lifecycle.addObserver(this)
  }

  protected fun dispose(func: () -> Disposable) {
    compositeDisposable.add(func())
  }

  @OnLifecycleEvent(ON_DESTROY)
  internal fun clear() {
    owner.lifecycle.removeObserver(this)
    compositeDisposable.clear()
    onCleared()
  }

  protected open fun onCleared() {

  }

}

