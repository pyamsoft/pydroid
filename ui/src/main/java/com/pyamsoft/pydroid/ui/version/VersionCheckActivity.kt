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

package com.pyamsoft.pydroid.ui.version

import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import com.google.android.material.snackbar.Snackbar
import com.pyamsoft.pydroid.bootstrap.version.VersionCheckProvider
import com.pyamsoft.pydroid.bootstrap.version.VersionCheckViewModel
import com.pyamsoft.pydroid.core.addTo
import com.pyamsoft.pydroid.core.disposable
import com.pyamsoft.pydroid.core.tryDispose
import com.pyamsoft.pydroid.ui.PYDroid
import com.pyamsoft.pydroid.ui.app.activity.ActivityBase
import com.pyamsoft.pydroid.ui.util.Snackbreak
import com.pyamsoft.pydroid.ui.util.show
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber

abstract class VersionCheckActivity : ActivityBase(), VersionCheckProvider {

  internal lateinit var viewModel: VersionCheckViewModel

  abstract val rootView: View

  private val compositeDisposable = CompositeDisposable()
  private var updateDisposable by disposable()

  @CallSuper
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    PYDroid.obtain(this)
        .plusVersionCheckComponent(currentApplicationVersion)
        .inject(this)

    observeUpdates()
  }

  private fun observeUpdates() {
    viewModel.onUpdateAvailable { wrapper ->
      wrapper.onLoading { onCheckingForUpdates(it) }
      wrapper.onSuccess { onUpdatedVersionFound(currentApplicationVersion, it) }
      wrapper.onError { onUpdatedVersionError(it) }
    }
        .addTo(compositeDisposable)
  }

  private fun onCheckingForUpdates(showSnackbar: Boolean) {
    if (showSnackbar) {
      Snackbreak.make(rootView, "Checking for updates...", Snackbar.LENGTH_SHORT)
          .show()
    }
  }

  // Start in post resume in case dialog launches before resume() is complete for fragments
  override fun onPostResume() {
    super.onPostResume()
    updateDisposable = viewModel.checkForUpdates(false)
  }

  @CallSuper
  override fun onPause() {
    super.onPause()
    updateDisposable.tryDispose()
  }

  @CallSuper
  override fun onDestroy() {
    super.onDestroy()
    compositeDisposable.clear()
  }

  private fun onUpdatedVersionFound(
    current: Int,
    updated: Int
  ) {
    Timber.d("Updated version found. %d => %d", current, updated)
    VersionUpgradeDialog.newInstance(applicationName, current, updated)
        .show(this, VersionUpgradeDialog.TAG)
  }

  private fun onUpdatedVersionError(throwable: Throwable) {
    // Silently drop version check errors
    Timber.e(throwable)
  }
}
