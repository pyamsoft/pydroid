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

package com.pyamsoft.pydroid.ui.internal.otherapps

import android.view.ViewGroup
import androidx.annotation.CheckResult
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.pyamsoft.pydroid.arch.BaseUiView
import com.pyamsoft.pydroid.bootstrap.otherapps.api.OtherApp
import com.pyamsoft.pydroid.ui.databinding.OtherAppsListBinding
import com.pyamsoft.pydroid.ui.internal.otherapps.listitem.OtherAppsAdapter
import com.pyamsoft.pydroid.ui.internal.otherapps.listitem.OtherAppsItemViewEvent.OpenStore
import com.pyamsoft.pydroid.ui.internal.otherapps.listitem.OtherAppsItemViewEvent.ViewSource
import com.pyamsoft.pydroid.ui.internal.otherapps.listitem.OtherAppsItemViewState
import com.pyamsoft.pydroid.ui.util.Snackbreak
import com.pyamsoft.pydroid.ui.util.removeAllItemDecorations
import com.pyamsoft.pydroid.util.asDp
import io.cabriole.decorator.LinearMarginDecoration
import me.zhanghai.android.fastscroll.FastScrollerBuilder

internal class OtherAppsList internal constructor(
    private val owner: LifecycleOwner,
    parent: ViewGroup
) : BaseUiView<OtherAppsViewState, OtherAppsViewEvent, OtherAppsListBinding>(parent) {

    override val viewBinding = OtherAppsListBinding::inflate

    override val layoutRoot by boundView { otherAppsList }

    private var listAdapter: OtherAppsAdapter? = null

    private var lastViewed: Int = 0

    init {
        doOnInflate {
            setupListView()
        }

        doOnInflate { savedInstanceState ->
            lastViewed = savedInstanceState.getOrDefault(KEY_CURRENT, 0)
        }

        doOnTeardown {
            binding.otherAppsList.adapter = null
            clearApps()
            listAdapter = null
        }

        doOnInflate {
            val margin = 8.asDp(binding.otherAppsList.context)
            LinearMarginDecoration.create(margin = margin).apply {
                binding.otherAppsList.addItemDecoration(this)
            }
        }

        doOnTeardown {
            binding.otherAppsList.removeAllItemDecorations()
        }

        doOnSaveState { outState ->
            outState.put(KEY_CURRENT, getCurrentPosition())
        }
    }

    @CheckResult
    private fun getCurrentPosition(): Int {
        val manager = binding.otherAppsList.layoutManager
        return if (manager is LinearLayoutManager) manager.findFirstVisibleItemPosition() else 0
    }

    private fun setupListView() {
        listAdapter = OtherAppsAdapter(owner) { event, index ->
            return@OtherAppsAdapter when (event) {
                is OpenStore -> publish(OtherAppsViewEvent.OpenStore(index))
                is ViewSource -> publish(OtherAppsViewEvent.ViewSource(index))
            }
        }

        binding.otherAppsList.apply {
            adapter = listAdapter
            layoutManager = LinearLayoutManager(context).apply {
                initialPrefetchItemCount = 3
                isItemPrefetchEnabled = false
            }
        }

        FastScrollerBuilder(binding.otherAppsList)
            .useMd2Style()
            .setPopupTextProvider(listAdapter)
            .build()
    }

    override fun onRender(state: OtherAppsViewState) {
        handleApps(state)
        handleNavigationError(state)
    }

    private fun handleNavigationError(state: OtherAppsViewState) {
        state.navigationError.let { throwable ->
            if (throwable == null) {
                clearNavigationError()
            } else {
                showNavigationError(throwable)
            }
        }
    }

    private fun handleApps(state: OtherAppsViewState) {
        state.apps.let { apps ->
            val beganEmpty = isEmpty()
            if (apps.isEmpty()) {
                clearApps()
            } else {
                loadApps(apps)
            }

            if (beganEmpty && !isEmpty()) {
                scrollToLastViewedItem()
            }
        }
    }

    private fun scrollToLastViewedItem() {
        val viewed = lastViewed
        if (viewed > 0) {
            lastViewed = 0
            binding.otherAppsList.scrollToPosition(viewed)
        }
    }

    @CheckResult
    private fun isEmpty(): Boolean {
        return requireNotNull(listAdapter).itemCount == 0
    }

    private fun loadApps(apps: List<OtherApp>) {
        requireNotNull(listAdapter).submitList(apps.map { OtherAppsItemViewState(it) })
    }

    private fun showNavigationError(error: Throwable) {
        Snackbreak.bindTo(owner, "navigate") {
            make(layoutRoot, error.message ?: "An unexpected error occurred.")
        }
    }

    private fun clearNavigationError() {
        Snackbreak.bindTo(owner, "navigate") {
            dismiss()
        }
    }

    private fun clearApps() {
        requireNotNull(listAdapter).submitList(null)
    }

    companion object {

        private const val KEY_CURRENT = "key_current_app"
    }
}
