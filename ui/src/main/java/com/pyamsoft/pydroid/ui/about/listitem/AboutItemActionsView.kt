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

import android.os.Bundle
import android.view.ViewGroup
import com.pyamsoft.pydroid.bootstrap.libraries.OssLibrary
import com.pyamsoft.pydroid.core.bus.Publisher
import com.pyamsoft.pydroid.ui.about.AboutViewEvent
import com.pyamsoft.pydroid.ui.about.AboutViewEvent.ViewLicense
import com.pyamsoft.pydroid.ui.about.AboutViewEvent.VisitHomepage
import com.pyamsoft.pydroid.ui.arch.UiView
import com.pyamsoft.pydroid.ui.databinding.AboutItemActionsBinding
import com.pyamsoft.pydroid.ui.util.setOnDebouncedClickListener

internal class AboutItemActionsView internal constructor(
  private val parent: ViewGroup,
  bus: Publisher<AboutViewEvent>
) : UiView<AboutViewEvent>(bus), BaseAboutItem {

  private lateinit var binding: AboutItemActionsBinding

  override fun id(): Int {
    return binding.aboutActions.id
  }

  override fun inflate(savedInstanceState: Bundle?) {
    binding = AboutItemActionsBinding.inflate(parent.inflater(), parent, true)
  }

  override fun teardown() {
    binding.unbind()
  }

  override fun saveState(outState: Bundle) {
  }

  override fun bind(model: OssLibrary) {
    binding.apply {
      actionViewLicense.setOnDebouncedClickListener {
        publish(ViewLicense(model.name, model.licenseUrl))
      }

      actionVisitHomepage.setOnDebouncedClickListener {
        publish(VisitHomepage(model.name, model.libraryUrl))
      }
    }
  }

  override fun unbind() {
    binding.apply {
      actionViewLicense.setOnDebouncedClickListener(null)
      actionVisitHomepage.setOnDebouncedClickListener(null)
    }
  }

}