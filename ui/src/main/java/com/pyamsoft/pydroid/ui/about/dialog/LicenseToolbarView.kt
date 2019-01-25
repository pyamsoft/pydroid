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

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pydroid.core.bus.Publisher
import com.pyamsoft.pydroid.loader.ImageLoader
import com.pyamsoft.pydroid.loader.ImageTarget
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.about.dialog.LicenseViewEvent.ToolbarMenuClick
import com.pyamsoft.pydroid.ui.about.dialog.LicenseViewEvent.ToolbarNavClick
import com.pyamsoft.pydroid.ui.arch.BaseUiView
import com.pyamsoft.pydroid.ui.arch.UiView
import com.pyamsoft.pydroid.ui.util.DebouncedOnClickListener
import com.pyamsoft.pydroid.ui.util.setUpEnabled

internal class LicenseToolbarView internal constructor(
  parent: ViewGroup,
  private val name: String,
  private val link: String,
  private val imageLoader: ImageLoader,
  private val owner: LifecycleOwner,
  uiBus: Publisher<LicenseViewEvent>
) : BaseUiView<LicenseViewEvent>(parent, uiBus) {

  private val toolbar by lazyView<Toolbar>(R.id.license_toolbar)

  override val layout: Int = R.layout.license_toolbar

  override fun id(): Int {
    return toolbar.id
  }

  override fun onInflated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    setupToolbar()
  }

  override fun teardown() {
    toolbar.setNavigationOnClickListener(null)
  }

  private fun setupToolbar() {
    toolbar.title = name
    toolbar.inflateMenu(R.menu.oss_library_menu)

    loadNavIcon()
    setupOnClick()
    setupMenuOnClick()
  }

  private fun loadNavIcon() {
    imageLoader.load(R.drawable.ic_close_24dp)
        .into(object : ImageTarget<Drawable> {

          override fun view(): View {
            return toolbar
          }

          override fun clear() {
            toolbar.navigationIcon = null
          }

          override fun setImage(image: Drawable) {
            toolbar.setUpEnabled(true, image)
          }

          override fun setError(error: Drawable?) {
            toolbar.setUpEnabled(false)
          }

          override fun setPlaceholder(placeholder: Drawable?) {
            toolbar.setUpEnabled(false)
          }

        })
        .bind(owner)
  }

  private fun setupOnClick() {
    toolbar.setNavigationOnClickListener(DebouncedOnClickListener.create {
      publish(ToolbarNavClick)
    })
  }

  private fun setupMenuOnClick() {
    toolbar.setOnMenuItemClickListener {
      publish(ToolbarMenuClick(it.itemId, link))
      return@setOnMenuItemClickListener true
    }
  }

}
