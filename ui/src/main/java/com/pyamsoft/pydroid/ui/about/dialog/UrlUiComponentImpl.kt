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

import android.content.ActivityNotFoundException
import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pydroid.arch.BaseUiComponent
import com.pyamsoft.pydroid.arch.doOnDestroy
import com.pyamsoft.pydroid.ui.about.dialog.UrlUiComponent.Callback
import com.pyamsoft.pydroid.ui.navigation.FailedNavigationPresenter
import com.pyamsoft.pydroid.ui.widget.shadow.DropshadowView
import com.pyamsoft.pydroid.ui.widget.spinner.SpinnerView

internal class UrlUiComponentImpl internal constructor(
  private val rootView: ConstraintLayout,
  private val toolbar: UrlToolbarView,
  private val webview: UrlWebviewView,
  private val dropshadow: DropshadowView,
  private val spinner: SpinnerView,
  private val presenter: UrlPresenter,
  private val failedNavigationPresenter: FailedNavigationPresenter
) : BaseUiComponent<UrlUiComponent.Callback>(),
    UrlUiComponent,
    UrlPresenter.Callback {

  override fun onBind(
    owner: LifecycleOwner,
    savedInstanceState: Bundle?,
    callback: Callback
  ) {
    owner.doOnDestroy {
      toolbar.teardown()
      webview.teardown()
      spinner.teardown()
      dropshadow.teardown()
      presenter.unbind()
    }

    toolbar.inflate(savedInstanceState)
    webview.inflate(savedInstanceState)
    spinner.inflate(savedInstanceState)
    dropshadow.inflate(savedInstanceState)
    presenter.bind(this)

    applyConstraints(rootView)

    // This looks weird because the webview is the state controller and the view...
    webview.loadUrl()
  }

  private fun applyConstraints(layoutRoot: ConstraintLayout) {
    ConstraintSet().apply {
      clone(layoutRoot)

      toolbar.also {
        connect(it.id(), ConstraintSet.TOP, layoutRoot.id, ConstraintSet.TOP)
        connect(it.id(), ConstraintSet.START, layoutRoot.id, ConstraintSet.START)
        connect(it.id(), ConstraintSet.END, layoutRoot.id, ConstraintSet.END)
        constrainWidth(it.id(), ConstraintSet.MATCH_CONSTRAINT)
      }

      dropshadow.also {
        connect(it.id(), ConstraintSet.TOP, toolbar.id(), ConstraintSet.BOTTOM)
        connect(it.id(), ConstraintSet.START, layoutRoot.id, ConstraintSet.START)
        connect(it.id(), ConstraintSet.END, layoutRoot.id, ConstraintSet.END)
        constrainWidth(it.id(), ConstraintSet.MATCH_CONSTRAINT)
      }

      webview.also {
        connect(it.id(), ConstraintSet.TOP, toolbar.id(), ConstraintSet.BOTTOM)
        connect(it.id(), ConstraintSet.START, layoutRoot.id, ConstraintSet.START)
        connect(it.id(), ConstraintSet.END, layoutRoot.id, ConstraintSet.END)
        connect(it.id(), ConstraintSet.BOTTOM, layoutRoot.id, ConstraintSet.BOTTOM)
        constrainHeight(it.id(), ConstraintSet.MATCH_CONSTRAINT)
        constrainWidth(it.id(), ConstraintSet.MATCH_CONSTRAINT)
      }

      spinner.also {
        connect(it.id(), ConstraintSet.TOP, toolbar.id(), ConstraintSet.BOTTOM)
        connect(it.id(), ConstraintSet.START, layoutRoot.id, ConstraintSet.START)
        connect(it.id(), ConstraintSet.END, layoutRoot.id, ConstraintSet.END)
        connect(it.id(), ConstraintSet.BOTTOM, layoutRoot.id, ConstraintSet.BOTTOM)
        constrainHeight(it.id(), ConstraintSet.MATCH_CONSTRAINT)
        constrainWidth(it.id(), ConstraintSet.MATCH_CONSTRAINT)
      }

      applyTo(layoutRoot)
    }
  }

  override fun saveState(outState: Bundle) {
    toolbar.saveState(outState)
    webview.saveState(outState)
    dropshadow.saveState(outState)
    spinner.saveState(outState)
  }

  override fun onWebviewBegin() {
    webview.hide()
    spinner.show()
  }

  override fun onWebviewOtherPageLoaded(url: String) {
    webview.hide()
    spinner.show()
  }

  override fun onWebviewTargetPageLoaded(url: String) {
    webview.hide()
    spinner.show()
  }

  override fun onWebviewExternalNavigationEvent(url: String) {
    callback.onNavigateToExternalUrl(url)
  }

  override fun onNavigateEvent() {
    callback.onCancelViewing()
  }

  override fun onViewLicenseExternal(url: String) {
    callback.onNavigateToExternalUrl(url)
  }

  override fun navigationFailed(error: ActivityNotFoundException) {
    failedNavigationPresenter.failedNavigation(error)
  }

}