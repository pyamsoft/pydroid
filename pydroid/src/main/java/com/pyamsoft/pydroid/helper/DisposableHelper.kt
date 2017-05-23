/*
 * Copyright 2017 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.helper

import android.support.annotation.CheckResult
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables

class DisposableHelper private constructor() {

  init {
    throw RuntimeException("No instances")
  }

  companion object {

    @JvmStatic @CheckResult fun dispose(disposable: Disposable?): Disposable {
      if (disposable == null) {
        return Disposables.empty()
      }

      if (!disposable.isDisposed) {
        disposable.dispose()
      }

      return Disposables.empty()
    }
  }
}
