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
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.CheckResult
import androidx.annotation.StringRes
import com.pyamsoft.pydroid.arch.BaseUiView
import com.pyamsoft.pydroid.bootstrap.libraries.OssLibrary
import com.pyamsoft.pydroid.ui.R
import javax.inject.Inject

internal class AboutItemTitleView @Inject internal constructor(
  private val model: OssLibrary,
  parent: ViewGroup
) : BaseUiView<Unit>(parent, Unit) {

  private val title by lazyView<TextView>(R.id.title)
  private val license by lazyView<TextView>(R.id.license)

  override val layout: Int = R.layout.about_item_title

  override val layoutRoot by lazyView<View>(R.id.about_title)

  override fun onInflated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    title.text = model.name
    license.text = getString(R.string.license_name, model.licenseName)
  }

  override fun onTeardown() {
    title.text = ""
    license.text = ""
  }

  @CheckResult
  private fun getString(@StringRes id: Int, vararg formatArgs: Any): String {
    return layoutRoot.context.getString(id, *formatArgs)
  }

}

