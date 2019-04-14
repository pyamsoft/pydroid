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

package com.pyamsoft.pydroid.ui.about.listitem

import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.arch.UiEventHandler
import com.pyamsoft.pydroid.bootstrap.SchedulerProvider
import com.pyamsoft.pydroid.core.bus.EventBus
import com.pyamsoft.pydroid.ui.about.listitem.AboutItemHandler.AboutItemHandlerEvent
import com.pyamsoft.pydroid.ui.about.listitem.AboutItemHandler.AboutItemHandlerEvent.ViewLicense
import com.pyamsoft.pydroid.ui.about.listitem.AboutItemHandler.AboutItemHandlerEvent.VisitHomepage
import io.reactivex.disposables.Disposable
import javax.inject.Inject

internal class AboutItemHandler @Inject internal constructor(
  private val schedulerProvider: SchedulerProvider,
  bus: EventBus<AboutItemHandlerEvent>
) : UiEventHandler<AboutItemHandlerEvent, AboutItemActionsView.Callback>(bus),
    AboutItemActionsView.Callback {

  override fun onViewLicenseClicked(
    name: String,
    licenseUrl: String
  ) {
    publish(ViewLicense(name, licenseUrl))
  }

  override fun onVisitHomepageClicked(
    name: String,
    homepageUrl: String
  ) {
    publish(VisitHomepage(name, homepageUrl))
  }

  @CheckResult
  override fun handle(delegate: AboutItemActionsView.Callback): Disposable {
    return listen()
        .subscribeOn(schedulerProvider.backgroundScheduler)
        .observeOn(schedulerProvider.foregroundScheduler)
        .subscribe {
          return@subscribe when (it) {
            is ViewLicense -> delegate.onViewLicenseClicked(it.name, it.url)
            is VisitHomepage -> delegate.onVisitHomepageClicked(it.name, it.url)
          }
        }
  }

  sealed class AboutItemHandlerEvent {
    data class ViewLicense(
      val name: String,
      val url: String
    ) : AboutItemHandlerEvent()

    data class VisitHomepage(
      val name: String,
      val url: String
    ) : AboutItemHandlerEvent()
  }

}