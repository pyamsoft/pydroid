/*
 * Copyright (C) 2018 Peter Kenji Yamanaka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pyamsoft.pydroid.bootstrap.version

import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pydroid.core.viewmodel.BaseViewModel
import com.pyamsoft.pydroid.core.viewmodel.DataBus
import com.pyamsoft.pydroid.core.viewmodel.DataWrapper
import io.reactivex.Scheduler
import timber.log.Timber

class VersionCheckViewModel internal constructor(
  owner: LifecycleOwner,
  private val updateBus: DataBus<Int>,
  private val packageName: String,
  private val currentVersionCode: Int,
  private val interactor: VersionCheckInteractor,
  private val foregroundScheduler: Scheduler,
  private val backgroundScheduler: Scheduler
) : BaseViewModel(owner) {

  private var checkUpdateDisposable by disposable()

  override fun onCleared() {
    super.onCleared()
    checkUpdateDisposable.tryDispose()
  }

  fun onUpdateAvailable(func: (DataWrapper<Int>) -> Unit) {
    dispose {
      updateBus.listen()
          .subscribeOn(backgroundScheduler)
          .observeOn(foregroundScheduler)
          .subscribe(func)
    }
  }

  fun checkForUpdates(force: Boolean) {
    checkUpdateDisposable = interactor.checkVersion(force, packageName)
        .subscribeOn(backgroundScheduler)
        .observeOn(foregroundScheduler)
        .filter { currentVersionCode < it }
        .doOnSubscribe { updateBus.publishLoading(force) }
        .doAfterTerminate { updateBus.publishComplete() }
        .subscribe({ updateBus.publishSuccess(it) }, {
          Timber.e(it, "Error checking for latest version")
          updateBus.publishError(it)
        })
  }
}
