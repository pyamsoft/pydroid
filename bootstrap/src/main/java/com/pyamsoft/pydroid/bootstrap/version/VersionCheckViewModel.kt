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

import androidx.annotation.CheckResult
import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable
import timber.log.Timber

class VersionCheckViewModel internal constructor(
  private val packageName: String,
  private val interactor: VersionCheckInteractor,
  private val foregroundScheduler: Scheduler,
  private val backgroundScheduler: Scheduler
) {

  @CheckResult
  fun checkForUpdates(
    force: Boolean,
    onCheckBegin: (forced: Boolean) -> Unit,
    onCheckSuccess: (newVersion: Int) -> Unit,
    onCheckError: (error: Throwable) -> Unit,
    onCheckComplete: () -> Unit
  ): Disposable {
    return interactor.checkVersion(force, packageName)
        .subscribeOn(backgroundScheduler)
        .observeOn(foregroundScheduler)
        .doOnSubscribe { onCheckBegin(force) }
        .doAfterTerminate { onCheckComplete() }
        .subscribe({ onCheckSuccess(it) }, {
          Timber.e(it, "Error checking for latest version")
          onCheckError(it)
        })
  }
}
