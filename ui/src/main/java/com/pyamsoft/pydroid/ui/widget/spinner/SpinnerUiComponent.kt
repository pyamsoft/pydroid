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

package com.pyamsoft.pydroid.ui.widget.spinner

import android.os.Bundle
import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pydroid.core.bus.Listener
import com.pyamsoft.pydroid.ui.arch.StateEvent
import com.pyamsoft.pydroid.ui.arch.UiComponent
import com.pyamsoft.pydroid.ui.arch.ViewEvent.EMPTY
import com.pyamsoft.pydroid.ui.arch.destroy
import io.reactivex.Observable

class SpinnerUiComponent<T : StateEvent, Show : T, Hide : T>(
  private val spinnerView: SpinnerView,
  private val owner: LifecycleOwner,
  private val controllerBus: Listener<T>,
  private val showTypeClass: Class<Show>,
  private val hideTypeClass: Class<Hide>
) : UiComponent<EMPTY> {

  override fun id(): Int {
    return spinnerView.id()
  }

  override fun create(savedInstanceState: Bundle?) {
    spinnerView.inflate(savedInstanceState)
    listenForControllerEvents()
  }

  private fun listenForControllerEvents() {
    controllerBus.listen()
        .subscribe {
          when (it::class.java) {
            showTypeClass -> spinnerView.show()
            hideTypeClass -> spinnerView.hide()
          }
        }
        .destroy(owner)
  }

  override fun saveState(outState: Bundle) {
    spinnerView.saveState(outState)
  }

  override fun onUiEvent(): Observable<EMPTY> {
    return Observable.empty()
  }

  companion object {

    @JvmStatic
    inline fun <T : StateEvent, reified Show : T, reified Hide : T> create(
      spinnerView: SpinnerView,
      owner: LifecycleOwner,
      controllerBus: Listener<T>
    ): SpinnerUiComponent<T, Show, Hide> {
      return SpinnerUiComponent(
          spinnerView, owner, controllerBus,
          Show::class.java, Hide::class.java
      )
    }
  }

}