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

package com.pyamsoft.pydroid.ui.otherapps.listitem

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.CheckResult
import androidx.constraintlayout.widget.ConstraintSet
import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pydroid.arch.ViewBinder
import com.pyamsoft.pydroid.arch.bindViews
import com.pyamsoft.pydroid.arch.doOnDestroy
import com.pyamsoft.pydroid.ui.Injector
import com.pyamsoft.pydroid.ui.PYDroidComponent
import com.pyamsoft.pydroid.ui.databinding.AdapterItemOtherAppsBinding
import com.pyamsoft.pydroid.ui.util.layout

internal class OtherAppsViewHolder private constructor(
    binding: AdapterItemOtherAppsBinding,
    owner: LifecycleOwner,
    callback: (event: OtherAppsItemViewEvent, index: Int) -> Unit
) : BaseViewHolder<AdapterItemOtherAppsBinding>(binding) {

    private val binder: ViewBinder<OtherAppsItemViewState>

    internal var titleView: OtherAppsItemTitleView? = null
    internal var iconView: OtherAppsItemIconView? = null
    internal var actionView: OtherAppsItemActionView? = null

    init {
        Injector.obtain<PYDroidComponent>(itemView.context.applicationContext)
            .plusOtherAppsItem()
            .create(binding.otherAppsListitemRoot)
            .inject(this)

        val title = requireNotNull(titleView)
        val icon = requireNotNull(iconView)
        val action = requireNotNull(actionView)
        binder = bindViews(
            owner,
            icon,
            title,
            action
        ) {
            callback(it, adapterPosition)
        }

        binding.otherAppsListitemRoot.layout {
            icon.let {
                connect(it.id(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
                connect(it.id(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
                connect(
                    it.id(),
                    ConstraintSet.BOTTOM,
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.BOTTOM
                )

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

        owner.doOnDestroy {
            titleView = null
            iconView = null
        }
    }

    override fun bind(state: OtherAppsItemViewState) {
        binder.bind(state)
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
