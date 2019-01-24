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
import com.pyamsoft.pydroid.bootstrap.SchedulerProvider
import com.pyamsoft.pydroid.core.bus.Listener
import com.pyamsoft.pydroid.ui.about.dialog.LicenseStateEvent.Complete
import com.pyamsoft.pydroid.ui.about.dialog.LicenseStateEvent.Loaded
import com.pyamsoft.pydroid.ui.about.dialog.LicenseStateEvent.Loading
import com.pyamsoft.pydroid.ui.about.dialog.LicenseStateEvent.PageError
import com.pyamsoft.pydroid.ui.arch.UiComponent
import com.pyamsoft.pydroid.ui.arch.destroy
import io.reactivex.Observable
import timber.log.Timber

internal class LicenseWebviewUiComponent internal constructor(
  private val webviewView: LicenseWebviewView,
  private val controllerBus: Listener<LicenseStateEvent>,
  private val schedulerProvider: SchedulerProvider,
  owner: LifecycleOwner
) : UiComponent<LicenseViewEvent>(owner) {

  override fun id(): Int {
    return webviewView.id()
  }

  override fun create(savedInstanceState: Bundle?) {
    webviewView.inflate(savedInstanceState)
    owner.runOnDestroy { webviewView.teardown() }

    listenForControllerEvents()
  }

  private fun listenForControllerEvents() {
    controllerBus.listen()
        .subscribeOn(schedulerProvider.backgroundScheduler)
        .observeOn(schedulerProvider.foregroundScheduler)
        .subscribe {
          return@subscribe when (it) {
            is Loading -> {
              webviewView.hide()
              webviewView.loadUrl()
            }
            is Loaded -> webviewView.show()
            is PageError -> webviewView.pageLoadError(it.error)
            is Complete -> Unit
            else -> Timber.d("Unhandled event: $it")
          }
        }
        .destroy(owner)
  }

  override fun saveState(outState: Bundle) {
    webviewView.saveState(outState)
  }

  override fun onUiEvent(): Observable<LicenseViewEvent> {
    return Observable.empty()
  }

}