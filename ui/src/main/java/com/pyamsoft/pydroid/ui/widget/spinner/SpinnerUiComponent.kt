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
import com.pyamsoft.pydroid.bootstrap.SchedulerProvider
import com.pyamsoft.pydroid.core.bus.Listener
import com.pyamsoft.pydroid.ui.arch.BaseUiComponent
import com.pyamsoft.pydroid.ui.arch.StateEvent
import com.pyamsoft.pydroid.ui.arch.ViewEvent.EMPTY
import com.pyamsoft.pydroid.ui.arch.ViewEvent.EmptyListener
import com.pyamsoft.pydroid.ui.arch.destroy
import timber.log.Timber

class SpinnerUiComponent<T : StateEvent, Show : T, Hide : T>(
  private val controllerBus: Listener<T>,
  private val showTypeClass: Class<Show>,
  private val hideTypeClass: Class<Hide>,
  view: SpinnerView,
  owner: LifecycleOwner,
  schedulerProvider: SchedulerProvider
) : BaseUiComponent<EMPTY, SpinnerView>(view, EmptyListener, owner, schedulerProvider) {

  override fun onCreate(savedInstanceState: Bundle?) {
    controllerBus.listen()
        .subscribeOn(schedulerProvider.backgroundScheduler)
        .observeOn(schedulerProvider.foregroundScheduler)
        .subscribe {
          return@subscribe when (it::class.java) {
            showTypeClass -> view.show()
            hideTypeClass -> view.hide()
            else -> Timber.d("Unhandled event: $it")
          }
        }
        .destroy(owner)
  }

  companion object {

    @JvmStatic
    inline fun <T : StateEvent, reified Show : T, reified Hide : T> create(
      owner: LifecycleOwner,
      spinnerView: SpinnerView,
      controllerBus: Listener<T>,
      schedulerProvider: SchedulerProvider = SchedulerProvider.DEFAULT
    ): SpinnerUiComponent<T, Show, Hide> {
      return SpinnerUiComponent(
          controllerBus, Show::class.java,
          Hide::class.java, spinnerView,
          owner, schedulerProvider
      )
    }
  }

}
