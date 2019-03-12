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

import android.os.Bundle
import androidx.lifecycle.LifecycleOwner

abstract class BaseUiComponent<C : Any> protected constructor() : UiComponent<C> {

  private var _callback: C? = null
  protected val callback: C
    get() = requireNotNull(_callback)

  final override fun bind(
    owner: LifecycleOwner,
    savedInstanceState: Bundle?,
    callback: C
  ) {
    this._callback = callback
    owner.doOnDestroy { this._callback = null }

    onBind(owner, savedInstanceState, callback)
  }

  protected abstract fun onBind(
    owner: LifecycleOwner,
    savedInstanceState: Bundle?,
    callback: C
  )

}
