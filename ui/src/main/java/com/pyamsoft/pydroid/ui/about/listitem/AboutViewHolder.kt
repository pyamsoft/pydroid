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

import com.pyamsoft.pydroid.bootstrap.libraries.OssLibrary
import com.pyamsoft.pydroid.ui.PYDroid
import com.pyamsoft.pydroid.ui.databinding.AdapterItemAboutLicenseBinding

internal class AboutViewHolder internal constructor(
  binding: AdapterItemAboutLicenseBinding
) : BaseViewHolder(binding.root) {

  internal lateinit var titleComponent: AboutItemTitleUiComponent
  internal lateinit var actionsComponent: AboutItemActionsUiComponent
  internal lateinit var descriptionComponent: AboutItemDescriptionUiComponent

  init {
    PYDroid.obtain(itemView.context.applicationContext)
        .plusAboutItemComponent(binding.listitemRoot)
        .inject(this)

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

}

