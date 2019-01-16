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
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.CheckResult
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.R2
import com.pyamsoft.pydroid.ui.arch.UiView
import com.pyamsoft.pydroid.ui.arch.ViewEvent.EMPTY
import com.pyamsoft.pydroid.ui.arch.ViewEvent.EmptyPublisher

internal class VersionUpgradeContentView internal constructor(
  private val parent: ViewGroup,
  private val applicationName: String,
  private val currentVersion: Int,
  private val newVersion: Int
) : UiView<EMPTY>(EmptyPublisher) {

  private lateinit var unbinder: Unbinder
  @field:BindView(R2.id.layout_root) internal lateinit var layoutRoot: ConstraintLayout
  @field:BindView(R2.id.upgrade_message) internal lateinit var message: TextView
  @field:BindView(R2.id.upgrade_current_value) internal lateinit var currentValue: TextView
  @field:BindView(R2.id.upgrade_new_value) internal lateinit var newValue: TextView

  override fun id(): Int {
    return layoutRoot.id
  }

  override fun inflate(savedInstanceState: Bundle?) {
    val root = parent.inflateAndAdd(R.layout.version_upgrade_content)
    unbinder = ButterKnife.bind(this, root)

    setApplicationMessage()
    setVersions()
  }

  override fun teardown() {
    unbinder.unbind()
  }

  override fun saveState(outState: Bundle) {
  }

  @CheckResult
  private fun getString(@StringRes id: Int, vararg formatArgs: Any): String {
    return layoutRoot.context.getString(id, *formatArgs)
  }

  private fun setApplicationMessage() {
    message.text = getString(R.string.upgrade_available_message, applicationName)
  }

  private fun setVersions() {
    currentValue.text = "$currentVersion"
    newValue.text = "$newVersion"
  }

}