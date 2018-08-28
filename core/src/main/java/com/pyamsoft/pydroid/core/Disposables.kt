package com.pyamsoft.pydroid.core

import androidx.annotation.CheckResult
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun Disposable.tryDispose(): Boolean {
  if (isDisposed) {
    return false
  } else {
    dispose()
    return true
  }
}

fun Disposable.addTo(compositeDisposable: CompositeDisposable) {
  compositeDisposable.add(this)
}

@CheckResult
fun disposable(initializer: () -> Disposable): SingleDisposable {
  return disposable(initializer())
}

@CheckResult
fun disposable(disposable: Disposable = Disposables.disposed()): SingleDisposable {
  return SingleDisposable(disposable)
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
