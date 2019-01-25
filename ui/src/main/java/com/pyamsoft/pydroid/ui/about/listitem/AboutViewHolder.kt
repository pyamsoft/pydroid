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

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CheckResult
import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pydroid.bootstrap.libraries.OssLibrary
import com.pyamsoft.pydroid.ui.PYDroid
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.about.AboutViewEvent.ViewLicense
import com.pyamsoft.pydroid.ui.about.AboutViewEvent.VisitHomepage
import com.pyamsoft.pydroid.ui.arch.destroy

internal class AboutViewHolder private constructor(
  owner: LifecycleOwner,
  view: View
) : BaseViewHolder(view) {

  internal lateinit var worker: AboutItemWorker
  internal lateinit var titleComponent: AboutItemTitleUiComponent
  internal lateinit var actionsComponent: AboutItemActionsUiComponent
  internal lateinit var descriptionComponent: AboutItemDescriptionUiComponent

  init {
    val root: ViewGroup = view.findViewById(R.id.about_listitem_root)
    PYDroid.obtain(itemView.context.applicationContext)
        .plusAboutItemComponent(owner, root)
        .inject(this)

    actionsComponent.onUiEvent()
        .subscribe {
          return@subscribe when (it) {
            is VisitHomepage -> worker.broadcast(it)
            is ViewLicense -> worker.broadcast(it)
          }
        }
        .destroy(owner)

    titleComponent.create(null)
    actionsComponent.create(null)
    descriptionComponent.create(null)
  }

  override fun bind(model: OssLibrary) {
    titleComponent.bind(model)
    actionsComponent.bind(model)
    descriptionComponent.bind(model)
  }

  override fun unbind() {
    titleComponent.unbind()
    actionsComponent.unbind()
    descriptionComponent.unbind()
  }

  companion object {

    @CheckResult
    @JvmStatic
    fun create(
      owner: LifecycleOwner,
      inflater: LayoutInflater,
      container: ViewGroup
    ): AboutViewHolder {
      val view = inflater.inflate(R.layout.adapter_item_about_license, container, false)
      return AboutViewHolder(owner, view)
    }
  }

}

