/*
 * Copyright 2017 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.ui.app.activity

import android.support.annotation.CallSuper
import android.support.annotation.CheckResult
import com.pyamsoft.pydroid.presenter.Presenter

/**
 * Activity which manages and automatically disposes of the provided presenters
 */
abstract class DisposableActivity : BackPressConfirmActivity() {

  /**
   * Cache so that the bound presenter list cannot change once it is set
   */
  private var boundPresenterSnapshot: List<Presenter<*, *>>? = null

  /**
   * List of presenters to bind to this Activity
   */
  @CheckResult protected abstract fun provideBoundPresenters(): List<Presenter<*, *>>

  @CheckResult private fun getBoundPresenters(): List<Presenter<*, *>> {
    if (boundPresenterSnapshot == null) {
      boundPresenterSnapshot = provideBoundPresenters()
    }

    val presenters = boundPresenterSnapshot
    if (presenters == null) {
      throw IllegalStateException("Bound Presenter list cannot be NULL")
    } else {
      return presenters
    }
  }

  @CallSuper
  override fun onStop() {
    super.onStop()
    val presenters = getBoundPresenters()
    for (presenter in presenters) {
      presenter.stop()
    }
  }

  @CallSuper
  override fun onDestroy() {
    super.onDestroy()
    val presenters = getBoundPresenters()
    for (presenter in presenters) {
      presenter.destroy()
    }
  }

}

