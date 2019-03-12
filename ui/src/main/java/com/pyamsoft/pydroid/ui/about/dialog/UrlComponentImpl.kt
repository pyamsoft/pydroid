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

import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pydroid.bootstrap.SchedulerProvider
import com.pyamsoft.pydroid.core.bus.EventBus
import com.pyamsoft.pydroid.loader.ImageLoader
import com.pyamsoft.pydroid.ui.navigation.FailedNavigationEvent
import com.pyamsoft.pydroid.ui.navigation.FailedNavigationPresenterImpl
import com.pyamsoft.pydroid.ui.widget.shadow.DropshadowView
import com.pyamsoft.pydroid.ui.widget.spinner.SpinnerView

internal class UrlComponentImpl internal constructor(
  private val parent: ViewGroup,
  private val owner: LifecycleOwner,
  private val imageLoader: ImageLoader,
  private val link: String,
  private val name: String,
  private val bus: EventBus<UrlWebviewState>,
  private val schedulerProvider: SchedulerProvider,
  private val failedNavigationBus: EventBus<FailedNavigationEvent>
) : UrlComponent {

  override fun inject(dialog: ViewUrlDialog) {
    val presenter = UrlPresenterImpl(schedulerProvider, bus)
    val webviewView = UrlWebviewView(owner, link, bus, parent)
    val spinnerView = SpinnerView(parent)
    val failed = FailedNavigationPresenterImpl(schedulerProvider, failedNavigationBus)

    val toolbarPresenter = UrlToolbarPresenterImpl()
    val toolbarView = UrlToolbarView(parent, name, link, imageLoader, toolbarPresenter)
    val dropshadow = DropshadowView(parent)

    dialog.apply {
      this.component = UrlUiComponentImpl(webviewView, spinnerView, presenter, failed)
      this.toolbarComponent = UrlToolbarUiComponentImpl(toolbarView, dropshadow, toolbarPresenter)
    }
  }

}
