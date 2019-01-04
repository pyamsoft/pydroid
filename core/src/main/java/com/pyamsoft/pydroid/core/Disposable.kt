/*
 * Copyright 2019 Peter Kenji Yamanaka
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
 *
 */

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
