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
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.lifecycle.LifecycleOwner
import butterknife.BindView
import butterknife.ButterKnife
import com.pyamsoft.pydroid.bootstrap.libraries.OssLibrary
import com.pyamsoft.pydroid.ui.PYDroid
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.R2

internal class AboutViewHolder internal constructor(
  owner: LifecycleOwner,
  inflater: LayoutInflater,
  container: ViewGroup
) : BaseViewHolder(inflater.inflate(R.layout.adapter_item_about_license, container, false)) {

  @field:BindView(R2.id.listitem_root) internal lateinit var layoutRoot: LinearLayout

  internal lateinit var titleComponent: AboutItemTitleUiComponent
  internal lateinit var actionsComponent: AboutItemActionsUiComponent
  internal lateinit var descriptionComponent: AboutItemDescriptionUiComponent

  init {
    ButterKnife.bind(this, itemView)

    PYDroid.obtain(itemView.context.applicationContext)
        .plusAboutItemComponent(owner, layoutRoot)
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

