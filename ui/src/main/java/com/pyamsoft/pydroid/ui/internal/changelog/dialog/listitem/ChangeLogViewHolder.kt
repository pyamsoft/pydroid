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

package com.pyamsoft.pydroid.ui.internal.changelog.dialog.listitem

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.CheckResult
import androidx.recyclerview.widget.RecyclerView
import com.pyamsoft.pydroid.arch.ViewBinder
import com.pyamsoft.pydroid.arch.createViewBinder
import com.pyamsoft.pydroid.ui.Injector
import com.pyamsoft.pydroid.ui.PYDroidComponent
import com.pyamsoft.pydroid.ui.databinding.ListitemLinearHorizontalBinding

internal class ChangeLogViewHolder
private constructor(
    binding: ListitemLinearHorizontalBinding,
) : RecyclerView.ViewHolder(binding.root), ViewBinder<ChangeLogItemViewState> {

  private val binder: ViewBinder<ChangeLogItemViewState>

  internal var typeView: ChangeLogItemType? = null
  internal var textView: ChangeLogItemText? = null

  init {
    Injector.obtainFromApplication<PYDroidComponent>(itemView.context)
        .plusChangeLogDialogItem()
        .create(binding.listitemLinearH)
        .inject(this)

    val type = requireNotNull(typeView)
    val text = requireNotNull(textView)
    binder = createViewBinder(type, text) {}
  }

  override fun bindState(state: ChangeLogItemViewState) {
    binder.bindState(state)
  }

  override fun teardown() {
    binder.teardown()
    typeView = null
    textView = null
  }

  companion object {

    @CheckResult
    @JvmStatic
    fun create(
        inflater: LayoutInflater,
        container: ViewGroup,
    ): ChangeLogViewHolder {
      val binding = ListitemLinearHorizontalBinding.inflate(inflater, container, false)
      return ChangeLogViewHolder(binding)
    }
  }
}
