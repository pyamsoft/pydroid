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

package com.pyamsoft.pydroid.ui.about.dialog

import android.os.Bundle
import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pydroid.core.bus.Listener
import com.pyamsoft.pydroid.core.bus.Publisher
import com.pyamsoft.pydroid.ui.about.dialog.LicenseStateEvents.Loaded
import com.pyamsoft.pydroid.ui.about.dialog.LicenseStateEvents.Loading
import com.pyamsoft.pydroid.ui.about.dialog.LicenseStateEvents.PageError
import com.pyamsoft.pydroid.ui.arch.UiComponent
import com.pyamsoft.pydroid.ui.arch.destroy
import com.pyamsoft.pydroid.ui.widget.spinner.SpinnerStateEvents
import com.pyamsoft.pydroid.ui.widget.spinner.SpinnerStateEvents.Hide
import com.pyamsoft.pydroid.ui.widget.spinner.SpinnerStateEvents.Show
import io.reactivex.Observable

internal class LicenseWebviewUiComponent internal constructor(
  private val webviewView: LicenseWebviewView,
  private val owner: LifecycleOwner,
  private val controllerBus: Listener<LicenseStateEvents>,
  private val spinnerBus: Publisher<SpinnerStateEvents>
) : UiComponent<LicenseViewEvents> {

  override fun id(): Int {
    return webviewView.id()
  }

  override fun create(savedInstanceState: Bundle?) {
    webviewView.inflate(savedInstanceState)

    owner.runOnDestroy {
      webviewView.teardown()
    }

    listenForControllerEvents()
  }

  private fun listenForControllerEvents() {
    controllerBus.listen()
        .subscribe {
          when (it) {
            is Loading -> {
              webviewView.hide()
              startSpinner()
              webviewView.loadUrl()
            }
            is Loaded -> {
              stopSpinner()
              webviewView.show()
            }
            is PageError -> {
              stopSpinner()
              webviewView.pageLoadError(it.error)
            }
          }
        }
        .destroy(owner)
  }

  private fun startSpinner() {
    spinnerBus.publish(Show)
  }

  private fun stopSpinner() {
    spinnerBus.publish(Hide)
  }

  override fun saveState(outState: Bundle) {
    webviewView.saveState(outState)
  }

  override fun onUiEvent(): Observable<LicenseViewEvents> {
    return Observable.empty()
  }

}