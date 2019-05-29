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

package com.pyamsoft.pydroid.arch

import androidx.annotation.CheckResult
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

abstract class UiViewModelFactory protected constructor() : ViewModelProvider.Factory {

  final override fun <T : ViewModel?> create(modelClass: Class<T>): T {
    if (UiViewModel::class.java.isAssignableFrom(modelClass)) {
      @Suppress("UNCHECKED_CAST")
      val viewModelClass = modelClass as Class<out UiViewModel<*, *, *>>

      @Suppress("UNCHECKED_CAST")
      return viewModel(viewModelClass) as T
    } else {
      fail()
    }
  }

  protected fun fail(): Nothing {
    throw IllegalArgumentException("Factory can only handle classes that extend UiViewModel")
  }

  @CheckResult
  protected abstract fun <T : UiViewModel<*, *, *>> viewModel(modelClass: Class<T>): UiViewModel<*, *, *>

}
