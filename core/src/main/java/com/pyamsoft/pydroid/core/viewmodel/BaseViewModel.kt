package com.pyamsoft.pydroid.core.viewmodel

import androidx.annotation.CheckResult
import androidx.lifecycle.Lifecycle.Event.ON_DESTROY
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

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

  @CheckResult
  protected fun disposable(initializer: () -> Disposable): SingleDisposable {
    return disposable(initializer())
  }

  @CheckResult
  protected fun disposable(disposable: Disposable = Disposables.disposed()): SingleDisposable {
    return SingleDisposable(disposable)
  }

  protected class SingleDisposable internal constructor(
    private var disposable: Disposable
  ) : ReadWriteProperty<Any?, Disposable> {

    override fun getValue(
      thisRef: Any?,
      property: KProperty<*>
    ): Disposable {
      return disposable
    }

    override fun setValue(
      thisRef: Any?,
      property: KProperty<*>,
      value: Disposable
    ) {
      disposable.tryDispose()
      disposable = value
    }

  }

  companion object {

    fun Disposable.tryDispose(): Boolean {
      if (isDisposed) {
        return false
      } else {
        dispose()
        return true
      }
    }

  }
}

