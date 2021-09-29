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

package com.pyamsoft.pydroid.ui.internal.otherapps.listitem

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.CheckResult
import androidx.constraintlayout.widget.ConstraintSet
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.pyamsoft.pydroid.arch.ViewBinder
import com.pyamsoft.pydroid.arch.createViewBinder
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.inject.Injector
import com.pyamsoft.pydroid.ui.PYDroidComponent
import com.pyamsoft.pydroid.ui.databinding.AdapterItemOtherAppsBinding
import com.pyamsoft.pydroid.ui.util.layout
import com.pyamsoft.pydroid.util.doOnDestroy

internal class OtherAppsViewHolder
private constructor(
    binding: AdapterItemOtherAppsBinding,
    owner: LifecycleOwner,
    callback: (event: OtherAppsItemViewEvent, index: Int) -> Unit
) : RecyclerView.ViewHolder(binding.root), ViewBinder<OtherAppsItemViewState> {

  private val binder: ViewBinder<OtherAppsItemViewState>

  internal var titleView: OtherAppsItemTitleView? = null
  internal var iconView: OtherAppsItemIconView? = null
  internal var actionView: OtherAppsItemActionView? = null

  init {
    Injector.obtainFromApplication<PYDroidComponent>(itemView.context)
        .plusOtherAppsItem()
        .create(binding.otherAppsListitemRoot)
        .inject(this)

    val title = titleView.requireNotNull()
    val icon = iconView.requireNotNull()
    val action = actionView.requireNotNull()
    binder = createViewBinder(icon, title, action) { callback(it, bindingAdapterPosition) }

    binding.otherAppsListitemRoot.layout {
      icon.let {
        connect(it.id(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
        connect(it.id(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
        connect(it.id(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)

        constrainWidth(it.id(), ConstraintSet.WRAP_CONTENT)
        constrainHeight(it.id(), ConstraintSet.MATCH_CONSTRAINT)
      }

      title.let {
        connect(it.id(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
        connect(it.id(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
        connect(it.id(), ConstraintSet.START, icon.id(), ConstraintSet.END)

        constrainWidth(it.id(), ConstraintSet.MATCH_CONSTRAINT)
        constrainHeight(it.id(), ConstraintSet.WRAP_CONTENT)
      }

      action.let {
        connect(it.id(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
        connect(it.id(), ConstraintSet.START, icon.id(), ConstraintSet.END)
        connect(it.id(), ConstraintSet.TOP, title.id(), ConstraintSet.BOTTOM)

        constrainWidth(it.id(), ConstraintSet.MATCH_CONSTRAINT)
        constrainHeight(it.id(), ConstraintSet.WRAP_CONTENT)
      }
    }

    owner.doOnDestroy { teardown() }
  }

  override fun bindState(state: OtherAppsItemViewState) {
    binder.bindState(state)
  }

  override fun teardown() {
    binder.teardown()
    titleView = null
    iconView = null
    actionView = null
  }

  companion object {

    @CheckResult
    @JvmStatic
    fun create(
        inflater: LayoutInflater,
        container: ViewGroup,
        owner: LifecycleOwner,
        callback: (event: OtherAppsItemViewEvent, index: Int) -> Unit
    ): OtherAppsViewHolder {
      val binding = AdapterItemOtherAppsBinding.inflate(inflater, container, false)
      return OtherAppsViewHolder(binding, owner, callback)
    }
  }
}
