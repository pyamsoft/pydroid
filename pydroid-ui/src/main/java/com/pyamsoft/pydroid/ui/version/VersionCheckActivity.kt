/*
 *     Copyright (C) 2017 Peter Kenji Yamanaka
 *
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.pyamsoft.pydroid.ui.version

import android.os.Bundle
import android.support.annotation.CallSuper
import com.pyamsoft.pydroid.presenter.Presenter
import com.pyamsoft.pydroid.ui.PYDroid
import com.pyamsoft.pydroid.ui.app.activity.DisposableActivity
import com.pyamsoft.pydroid.ui.util.DialogUtil
import com.pyamsoft.pydroid.version.VersionCheckPresenter
import com.pyamsoft.pydroid.version.VersionCheckProvider
import timber.log.Timber

abstract class VersionCheckActivity : DisposableActivity(), VersionCheckProvider, VersionCheckPresenter.View {

  internal lateinit var presenter: VersionCheckPresenter

  @CallSuper
  override fun provideBoundPresenters(): List<Presenter<*>> = listOf(presenter)

  @CallSuper override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    PYDroid.obtain(this).plusVersionCheckComponent(packageName, currentApplicationVersion)
        .inject(this)
    presenter.bind(this)
  }

  // Start in post resume in case dialog launches before resume() is complete for fragments
  override fun onPostResume() {
    super.onPostResume()
    presenter.checkForUpdates(false)
  }

  override fun onUpdatedVersionFound(current: Int, updated: Int) {
    Timber.d("Updated version found. %d => %d", current, updated)
    DialogUtil.guaranteeSingleDialogFragment(this,
        VersionUpgradeDialog.newInstance(applicationName, current, updated),
        VersionUpgradeDialog.TAG)
  }
}
