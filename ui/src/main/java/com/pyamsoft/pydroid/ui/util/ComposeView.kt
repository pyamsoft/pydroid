/*
 * Copyright 2022 Peter Kenji Yamanaka
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

import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment

/**
 * Force a view to recompose by disposing and re-creating a Composition only if it is attached to a
 * window
 */
public fun ComposeView.recompose() {
  if (this.isAttachedToWindow) {
    this.disposeComposition()
    this.createComposition()
  }
}

/** Dispose a ComposeView only if it is attached to a window */
public fun ComposeView.dispose() {
  if (this.isAttachedToWindow) {
    this.disposeComposition()
  }
}

/** Force a view to recompose by disposing and re-creating a Composition */
public fun Fragment.recompose() {
  (view as? ComposeView)?.recompose()
}

/** Force a view to recompose by disposing and re-creating a Composition */
public fun Fragment.dispose() {
  (view as? ComposeView)?.dispose()
}
