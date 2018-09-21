package com.pyamsoft.pydroid.core

import androidx.annotation.CheckResult
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

@CheckResult
fun singleDisposable(initializer: () -> Disposable): SingleDisposable {
  return singleDisposable(initializer())
}

@CheckResult
fun singleDisposable(disposable: Disposable = Disposables.disposed()): SingleDisposable {
  return SingleDisposable(disposable)
}

fun Disposable.tryDispose(): Boolean {
  if (isDisposed) {
    return false
  } else {
    dispose()
    return true
  }
}

class SingleDisposable internal constructor(
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
