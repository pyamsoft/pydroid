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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.pyamsoft.pydroid.bootstrap.libraries.OssLibrary
import com.pyamsoft.pydroid.ui.PYDroid
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.util.fakeBind
import com.pyamsoft.pydroid.util.fakeUnbind

internal class AboutViewHolder private constructor(
  view: View,
  callback: AboutViewHolderUiComponent.Callback
) : BaseViewHolder(view), LifecycleOwner {

  private val registry = LifecycleRegistry(this)

  internal lateinit var component: AboutViewHolderUiComponent

  init {
    val root: ViewGroup = view.findViewById(R.id.about_listitem_root)
    PYDroid.obtain(itemView.context.applicationContext)
        .plusAboutItemComponent(root)
        .inject(this)

    component.bind(this, null, callback)
  }

  override fun getLifecycle(): Lifecycle {
    return registry
  }

  override fun bind(model: OssLibrary) {
    component.bind(this, model)
    registry.fakeBind()
  }

  override fun unbind() {
    registry.fakeUnbind()
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

