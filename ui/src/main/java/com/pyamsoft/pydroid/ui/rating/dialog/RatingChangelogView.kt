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

package com.pyamsoft.pydroid.ui.rating.dialog

import android.os.Bundle
import android.text.SpannedString
import android.view.ViewGroup
import android.widget.ScrollView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.R2
import com.pyamsoft.pydroid.ui.arch.UiView
import com.pyamsoft.pydroid.ui.arch.ViewEvent.EMPTY
import com.pyamsoft.pydroid.ui.arch.ViewEvent.EmptyPublisher

internal class RatingChangelogView internal constructor(
  private val parent: ViewGroup,
  private val changelog: SpannedString
) : UiView<EMPTY>(EmptyPublisher) {

  private lateinit var unbinder: Unbinder
  @field:BindView(R2.id.layout_root) internal lateinit var layoutRoot: ScrollView
  @field:BindView(R2.id.changelog) internal lateinit var changelogView: TextView

  override fun id(): Int {
    return layoutRoot.id
  }

  override fun inflate(savedInstanceState: Bundle?) {
    val root = parent.inflateAndAdd(R.layout.rating_changelog)
    unbinder = ButterKnife.bind(this, root)
    loadChangelog()
  }

  override fun teardown() {
    unbinder.unbind()
  }

  private fun loadChangelog() {
    changelogView.text = changelog
  }

  override fun saveState(outState: Bundle) {
  }

}