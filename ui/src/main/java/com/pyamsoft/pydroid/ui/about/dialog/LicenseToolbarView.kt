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
import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pydroid.core.bus.Publisher
import com.pyamsoft.pydroid.loader.ImageLoader
import com.pyamsoft.pydroid.loader.ImageTarget
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.about.dialog.LicenseViewEvent.ToolbarMenuClick
import com.pyamsoft.pydroid.ui.about.dialog.LicenseViewEvent.ToolbarNavClick
import com.pyamsoft.pydroid.ui.arch.UiView
import com.pyamsoft.pydroid.ui.databinding.LicenseToolbarBinding
import com.pyamsoft.pydroid.ui.util.DebouncedOnClickListener
import com.pyamsoft.pydroid.ui.util.setUpEnabled

internal class LicenseToolbarView internal constructor(
  private val parent: ViewGroup,
  private val name: String,
  private val link: String,
  private val imageLoader: ImageLoader,
  private val owner: LifecycleOwner,
  uiBus: Publisher<LicenseViewEvent>
) : UiView<LicenseViewEvent>(uiBus) {

  private lateinit var binding: LicenseToolbarBinding

  override fun id(): Int {
    return binding.licenseToolbar.id
  }

  override fun inflate(savedInstanceState: Bundle?) {
    binding = LicenseToolbarBinding.inflate(parent.inflater(), parent, true)
    setupToolbar()
  }

  override fun teardown() {
    binding.unbind()
  }

  override fun saveState(outState: Bundle) {
  }

  private fun setupToolbar() {
    binding.apply {
      licenseToolbar.title = name
      licenseToolbar.inflateMenu(R.menu.oss_library_menu)
    }

    loadNavIcon()
    setupOnClick()
    setupMenuOnClick()
  }

  private fun loadNavIcon() {
    binding.apply {
      imageLoader.load(R.drawable.ic_close_24dp)
          .into(object : ImageTarget<Drawable> {

            override fun view(): View {
              return licenseToolbar
            }

            override fun clear() {
              licenseToolbar.navigationIcon = null
            }

            override fun setImage(image: Drawable) {
              licenseToolbar.setUpEnabled(true, image)
            }

            override fun setError(error: Drawable?) {
              licenseToolbar.setUpEnabled(false)
            }

            override fun setPlaceholder(placeholder: Drawable?) {
              licenseToolbar.setUpEnabled(false)
            }

          })
          .bind(owner)
    }
  }

  private fun setupOnClick() {
    binding.licenseToolbar.setNavigationOnClickListener(DebouncedOnClickListener.create {
      publish(ToolbarNavClick)
    })
  }

  private fun setupMenuOnClick() {
    binding.licenseToolbar.setOnMenuItemClickListener {
      publish(ToolbarMenuClick(it.itemId, link))
      return@setOnMenuItemClickListener true
    }
  }

}