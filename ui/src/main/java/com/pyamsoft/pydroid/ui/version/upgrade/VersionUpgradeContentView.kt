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

package com.pyamsoft.pydroid.ui.version.upgrade

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.CheckResult
import androidx.annotation.StringRes
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.arch.BaseUiView

internal class VersionUpgradeContentView internal constructor(
  private val applicationName: String,
  private val currentVersion: Int,
  private val newVersion: Int,
  parent: ViewGroup
) : BaseUiView<Unit>(parent, Unit) {

  private val layoutRoot by lazyView<View>(R.id.version_content_root)
  private val upgradeMessage by lazyView<TextView>(R.id.upgrade_message)
  private val currentValue by lazyView<TextView>(R.id.upgrade_current_value)
  private val newValue by lazyView<TextView>(R.id.upgrade_new_value)

  override val layout: Int = R.layout.version_upgrade_content

  override fun id(): Int {
    return layoutRoot.id
  }

  override fun onInflated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    setApplicationMessage()
    setVersions()
  }

  override fun teardown() {
    upgradeMessage.text = ""
    currentValue.text = ""
    newValue.text = ""
  }

  @CheckResult
  private fun getString(@StringRes id: Int, vararg formatArgs: Any): String {
    return layoutRoot.context.getString(id, *formatArgs)
  }

  private fun setApplicationMessage() {
    upgradeMessage.text = getString(R.string.upgrade_available_message, applicationName)
  }

  private fun setVersions() {
    currentValue.text = "$currentVersion"
    newValue.text = "$newVersion"
  }

}
