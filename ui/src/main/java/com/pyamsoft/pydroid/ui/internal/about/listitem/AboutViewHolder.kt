/*
 * Copyright 2020 Peter Kenji Yamanaka
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
 */

package com.pyamsoft.pydroid.ui.internal.about.listitem

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.CheckResult
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.composethemeadapter.MdcTheme
import com.pyamsoft.pydroid.arch.ViewBinder
import com.pyamsoft.pydroid.ui.databinding.AboutListItemBinding
import com.pyamsoft.pydroid.util.doOnDestroy

internal class AboutViewHolder
private constructor(
    private val binding: AboutListItemBinding,
    owner: LifecycleOwner,
    private val callback: AboutAdapter.Callback,
) : RecyclerView.ViewHolder(binding.root), ViewBinder<AboutItemViewState> {

  init {
    owner.doOnDestroy { teardown() }
  }

  override fun bindState(state: AboutItemViewState) {
    binding.aboutListitemRoot.setContent {
      MdcTheme {
        AboutListItem(
            state = state,
            onClickViewHomePage = { callback.onViewHomepage(bindingAdapterPosition) },
            onClickViewLicense = { callback.onViewLicense(bindingAdapterPosition) },
        )
      }
    }
  }

  override fun teardown() {
    binding.aboutListitemRoot.disposeComposition()
  }

  companion object {

    @CheckResult
    @JvmStatic
    fun create(
        inflater: LayoutInflater,
        container: ViewGroup,
        owner: LifecycleOwner,
        callback: AboutAdapter.Callback,
    ): AboutViewHolder {
      val binding = AboutListItemBinding.inflate(inflater, container, false)
      return AboutViewHolder(binding, owner, callback)
    }
  }
}
