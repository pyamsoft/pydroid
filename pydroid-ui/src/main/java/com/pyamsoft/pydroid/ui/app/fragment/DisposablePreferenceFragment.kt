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

package com.pyamsoft.pydroid.ui.app.fragment

import android.support.annotation.CallSuper
import android.support.annotation.CheckResult
import com.pyamsoft.pydroid.presenter.Presenter

/**
 * Fragment which manages and automatically disposes of the provided presenters
 */
abstract class DisposablePreferenceFragment : ActionBarPreferenceFragment() {

  /**
   * Cache so that the bound presenter list cannot change once it is set
   */
  private var boundPresenterSnapshot: List<Presenter<*>>? = null

  /**
   * List of presenters to bind to this Fragment
   */
  @CheckResult protected abstract fun provideBoundPresenters(): List<Presenter<*>>

  @CheckResult private fun getBoundPresenters(): List<Presenter<*>> {
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
  override fun onDestroyView() {
    super.onDestroyView()
    val presenters = getBoundPresenters()
    for (presenter in presenters) {
      presenter.unbind()
    }
  }

}