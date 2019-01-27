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

package com.pyamsoft.pydroid.ui.version

import android.content.ActivityNotFoundException
import android.os.Bundle
import android.view.View
import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pydroid.ui.arch.InvalidUiComponentIdException
import com.pyamsoft.pydroid.ui.arch.UiView
import com.pyamsoft.pydroid.ui.arch.ViewEvent.EMPTY
import com.pyamsoft.pydroid.ui.util.Snackbreak
import io.reactivex.Observable

internal class VersionView internal constructor(
  private val view: View,
  private val owner: LifecycleOwner
) : UiView<EMPTY> {

  override fun id(): Int {
    throw InvalidUiComponentIdException
  }

  override fun inflate(savedInstanceState: Bundle?) {
  }

  override fun teardown() {
  }

  override fun saveState(outState: Bundle) {
  }

  override fun onUiEvent(): Observable<EMPTY> {
    return Observable.empty()
  }

  fun showUpdating() {
    Snackbreak.bindTo(owner)
        .short(view, "Checking for updates")
        .show()
  }

  fun dismissUpdating() {
    Snackbreak.bindTo(owner)
        .dismiss()
  }

  fun showError(error: ActivityNotFoundException) {
    Snackbreak.bindTo(owner)
        .short(view, error.message ?: "No activity found that can handle this URL")
        .show()
  }

}
