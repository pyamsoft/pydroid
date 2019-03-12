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
import com.pyamsoft.pydroid.arch.BaseUiView
import com.pyamsoft.pydroid.loader.ImageLoader
import com.pyamsoft.pydroid.loader.ImageTarget
import com.pyamsoft.pydroid.loader.Loaded
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.about.dialog.UrlToolbarView.Callback
import com.pyamsoft.pydroid.ui.util.DebouncedOnClickListener
import com.pyamsoft.pydroid.ui.util.setUpEnabled

internal class UrlToolbarView internal constructor(
  parent: ViewGroup,
  private val name: String,
  private val link: String,
  private val imageLoader: ImageLoader,
  callback: UrlToolbarView.Callback
) : BaseUiView<Callback>(parent, callback) {

  private var navIconLoaded: Loaded? = null

  override val layout: Int = R.layout.license_toolbar

  override val layoutRoot by lazyView<Toolbar>(R.id.license_toolbar)

  override fun onInflated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    setupToolbar()
  }

  override fun onTeardown() {
    layoutRoot.setNavigationOnClickListener(null)
    layoutRoot.setOnMenuItemClickListener(null)
    navIconLoaded?.dispose()
  }

  private fun setupToolbar() {
    layoutRoot.title = name
    layoutRoot.inflateMenu(R.menu.oss_library_menu)

    loadNavIcon()
    setupOnClick()
    setupMenuOnClick()
  }

  private fun loadNavIcon() {
    navIconLoaded?.dispose()
    navIconLoaded = imageLoader.load(R.drawable.ic_close_24dp)
        .into(object : ImageTarget<Drawable> {

          override fun view(): View {
            return layoutRoot
          }

          override fun clear() {
            layoutRoot.navigationIcon = null
          }

          override fun setImage(image: Drawable) {
            layoutRoot.setUpEnabled(true, image)
          }

          override fun setError(error: Drawable?) {
            layoutRoot.setUpEnabled(false)
          }

          override fun setPlaceholder(placeholder: Drawable?) {
            layoutRoot.setUpEnabled(false)
          }

        })
  }

  private fun setupOnClick() {
    layoutRoot.setNavigationOnClickListener(DebouncedOnClickListener.create {
      callback.onToolbarNavClicked()
    })
  }

  private fun setupMenuOnClick() {
    layoutRoot.setOnMenuItemClickListener {
      if (it.itemId == R.id.menu_item_view_license) {
        callback.onViewLicenseExternal(link)
      }
      return@setOnMenuItemClickListener true
    }
  }

  interface Callback {

    fun onViewLicenseExternal(url: String)

    fun onToolbarNavClicked()
  }

}
