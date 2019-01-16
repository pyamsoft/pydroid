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
import android.widget.TextView
import androidx.annotation.CheckResult
import androidx.annotation.StringRes
import com.pyamsoft.pydroid.bootstrap.libraries.OssLibrary
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.arch.UiView
import com.pyamsoft.pydroid.ui.arch.ViewEvent.EMPTY
import com.pyamsoft.pydroid.ui.arch.ViewEvent.EmptyPublisher

internal class AboutItemTitleView internal constructor(
  private val parent: ViewGroup
) : UiView<EMPTY>(EmptyPublisher), BaseAboutItem {

  private lateinit var layoutRoot: ViewGroup
  private lateinit var title: TextView
  private lateinit var license: TextView

  override fun id(): Int {
    return layoutRoot.id
  }

  override fun inflate(savedInstanceState: Bundle?) {
    parent.inflateAndAdd(R.layout.about_item_title) {
      layoutRoot = findViewById(R.id.about_title)
      title = findViewById(R.id.title)
      license = findViewById(R.id.license)
    }
  }

  override fun teardown() {
    unbind()
  }

  override fun saveState(outState: Bundle) {
  }

  @CheckResult
  private fun getString(@StringRes id: Int, vararg formatArgs: Any): String {
    return layoutRoot.context.getString(id, *formatArgs)
  }

  override fun bind(model: OssLibrary) {
    title.text = model.name
    license.text = getString(R.string.license_name, model.licenseName)
  }

  override fun unbind() {
    title.text = ""
    license.text = ""
  }

}

