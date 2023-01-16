/*
 * Copyright 2023 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.LifecycleObserver

/** Add an observer to the Local Lifecycle Owner */
@Composable
public fun LifecycleEffect(observer: () -> LifecycleObserver) {
  val createObserver = rememberUpdatedState(observer)
  val lifecycleOwner = LocalLifecycleOwner.current
  DisposableEffect(
      lifecycleOwner,
      createObserver,
  ) {
    val obs = createObserver.value.invoke()
    val lifecycle = lifecycleOwner.lifecycle
    lifecycle.addObserver(obs)
    onDispose { lifecycle.removeObserver(obs) }
  }
}
