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
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.pyamsoft.pydroid.arch.ViewBinder
import com.pyamsoft.pydroid.arch.createViewBinder
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.inject.Injector
import com.pyamsoft.pydroid.ui.PYDroidComponent
import com.pyamsoft.pydroid.ui.databinding.ListitemLinearHorizontalBinding
import com.pyamsoft.pydroid.util.doOnDestroy

internal class ChangeLogViewHolder
private constructor(
    binding: ListitemLinearHorizontalBinding,
    owner: LifecycleOwner,
) : RecyclerView.ViewHolder(binding.root), ViewBinder<ChangeLogItemViewState> {

  private val binder: ViewBinder<ChangeLogItemViewState>

  internal var typeView: ChangeLogItemType? = null
  internal var textView: ChangeLogItemText? = null

  init {
    Injector.obtainFromApplication<PYDroidComponent>(itemView.context)
        .plusChangeLogDialogItem()
        .create(binding.listitemLinearH)
        .inject(this)

    binder =
        createViewBinder(
            typeView.requireNotNull(),
            textView.requireNotNull(),
        ) {}

    owner.doOnDestroy { teardown() }
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
        owner: LifecycleOwner,
    ): ChangeLogViewHolder {
      val binding = ListitemLinearHorizontalBinding.inflate(inflater, container, false)
      return ChangeLogViewHolder(binding, owner)
    }
  }
}
