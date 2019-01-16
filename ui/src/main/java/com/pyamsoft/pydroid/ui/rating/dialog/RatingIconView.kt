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
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pydroid.loader.ImageLoader
import com.pyamsoft.pydroid.ui.arch.UiView
import com.pyamsoft.pydroid.ui.arch.ViewEvent.EMPTY
import com.pyamsoft.pydroid.ui.arch.ViewEvent.EmptyPublisher
import com.pyamsoft.pydroid.ui.databinding.RatingIconBinding

internal class RatingIconView internal constructor(
  private val parent: ViewGroup,
  private val changelogIcon: Int,
  private val imageLoader: ImageLoader,
  private val owner: LifecycleOwner
) : UiView<EMPTY>(EmptyPublisher) {

  private lateinit var binding: RatingIconBinding

  override fun id(): Int {
    return binding.ratingIconRoot.id
  }

  override fun inflate(savedInstanceState: Bundle?) {
    binding = RatingIconBinding.inflate(parent.inflater(), parent, true)

    loadIcon()
  }

  override fun teardown() {
    binding.unbind()
  }

  private fun loadIcon() {
    imageLoader.load(changelogIcon)
        .into(binding.icon)
        .bind(owner)
  }

  override fun saveState(outState: Bundle) {
  }

}