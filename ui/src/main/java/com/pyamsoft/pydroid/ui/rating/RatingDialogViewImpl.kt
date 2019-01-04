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

package com.pyamsoft.pydroid.ui.rating

import android.text.SpannedString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pydroid.loader.ImageLoader
import com.pyamsoft.pydroid.ui.databinding.DialogRatingBinding
import com.pyamsoft.pydroid.ui.util.setOnDebouncedClickListener
import com.pyamsoft.pydroid.util.toDp

internal class RatingDialogViewImpl internal constructor(
  private val inflater: LayoutInflater,
  private val container: ViewGroup?,
  private val imageLoader: ImageLoader,
  private val owner: LifecycleOwner,
  private val changeLogIcon: Int,
  private val changelog: SpannedString
) : RatingDialogView {

  private lateinit var binding: DialogRatingBinding

  override fun create() {
    binding = DialogRatingBinding.inflate(inflater, container, false)
    initDialog()
  }

  private fun initDialog() {
    binding.apply {
      // Set icon elevation
      ViewCompat.setElevation(
          ratingIcon,
          8.toDp(ratingIcon.context).toFloat()
      )

      // Load icon
      imageLoader.load(changeLogIcon)
          .into(ratingIcon)
          .bind(owner)

      // Load changelog
      ratingTextChange.text = changelog
    }
  }

  override fun root(): View {
    return binding.root
  }

  override fun onSaveRating(onSave: () -> Unit) {
    binding.ratingBtnGoRate.setOnDebouncedClickListener { onSave() }
  }

  override fun onCancelRating(onCancel: () -> Unit) {
    binding.ratingBtnNoThanks.setOnDebouncedClickListener { onCancel() }
  }

}