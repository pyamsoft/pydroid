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
import com.pyamsoft.pydroid.bootstrap.libraries.OssLibrary
import com.pyamsoft.pydroid.ui.PYDroid
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.app.ListItemLifecycle
import javax.inject.Inject

internal class AboutViewHolder private constructor(
  view: View,
  private val callback: AboutViewHolderUiComponent.Callback
) : BaseViewHolder(view) {

  @field:Inject internal lateinit var component: AboutViewHolderUiComponent

  private val parent = view.findViewById<ViewGroup>(R.id.about_listitem_root)
  private var bindLifecycle: ListItemLifecycle? = null

  override fun bind(model: OssLibrary) {
    bindLifecycle?.unbind()

    PYDroid.obtain(itemView.context.applicationContext)
        .plusAboutItem()
        .create(parent, model)
        .inject(this)

    val owner = ListItemLifecycle()
    bindLifecycle = owner

    component.bind(owner, null, callback)
  }

  override fun unbind() {
    bindLifecycle?.unbind()
    bindLifecycle = null
  }

  companion object {

    @CheckResult
    @JvmStatic
    fun create(
      inflater: LayoutInflater,
      container: ViewGroup,
      callback: AboutViewHolderUiComponent.Callback
    ): AboutViewHolder {
      val view = inflater.inflate(R.layout.adapter_item_about_license, container, false)
      return AboutViewHolder(view, callback)
    }
  }

}

