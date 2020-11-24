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

package com.pyamsoft.pydroid.ui.internal.changelog.dialog

import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.pyamsoft.pydroid.arch.BaseUiView
import com.pyamsoft.pydroid.ui.databinding.ChangelogListBinding
import com.pyamsoft.pydroid.ui.internal.changelog.ChangeLogLine
import com.pyamsoft.pydroid.ui.internal.changelog.dialog.listitem.ChangeLogAdapter
import com.pyamsoft.pydroid.ui.internal.changelog.dialog.listitem.ChangeLogItemViewState
import com.pyamsoft.pydroid.ui.util.removeAllItemDecorations
import com.pyamsoft.pydroid.util.asDp
import io.cabriole.decorator.LinearMarginDecoration
import me.zhanghai.android.fastscroll.FastScrollerBuilder

internal class ChangeLogList internal constructor(
    parent: ViewGroup
) : BaseUiView<ChangeLogDialogViewState, ChangeLogDialogViewEvent, ChangelogListBinding>(parent) {

    override val viewBinding = ChangelogListBinding::inflate

    override val layoutRoot by boundView { changelogList }

    private var changeLogAdapter: ChangeLogAdapter? = null

    init {
        doOnInflate {
            setupListView()
        }

        doOnTeardown {
            binding.changelogList.adapter = null
            clear()
            changeLogAdapter = null
        }

        doOnInflate {
            val margin = 8.asDp(binding.changelogList.context)
            LinearMarginDecoration.create(margin = margin).apply {
                binding.changelogList.addItemDecoration(this)
            }
        }

        doOnTeardown {
            binding.changelogList.removeAllItemDecorations()
        }
    }

    private fun setupListView() {
        changeLogAdapter = ChangeLogAdapter()

        binding.changelogList.apply {
            adapter = changeLogAdapter
            layoutManager = LinearLayoutManager(context).apply {
                initialPrefetchItemCount = 3
                isItemPrefetchEnabled = false
            }
        }

        FastScrollerBuilder(binding.changelogList)
            .useMd2Style()
            .setPopupTextProvider(changeLogAdapter)
            .build()
    }

    override fun onRender(state: ChangeLogDialogViewState) {
        handleLoading(state)
        handleChangeLog(state)
    }

    private fun handleLoading(state: ChangeLogDialogViewState) {
        state.changeLog.let { log ->
            if (log.isEmpty()) {
                hide()
            } else {
                show()
            }
        }
    }

    private fun handleChangeLog(state: ChangeLogDialogViewState) {
        state.changeLog.let { log ->
            if (log.isEmpty()) {
                clear()
            } else {
                loadChangeLog(log)
            }
        }
    }

    private fun show() {
        layoutRoot.isVisible = true
    }

    private fun hide() {
        layoutRoot.isVisible = false
    }

    private fun loadChangeLog(changeLog: List<ChangeLogLine>) {
        requireNotNull(changeLogAdapter).submitList(changeLog.map { ChangeLogItemViewState(it) })
    }

    private fun clear() {
        requireNotNull(changeLogAdapter).submitList(null)
    }
}
