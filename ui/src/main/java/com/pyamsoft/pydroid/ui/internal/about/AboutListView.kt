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

package com.pyamsoft.pydroid.ui.internal.about

import android.view.ViewGroup
import androidx.annotation.CheckResult
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.pyamsoft.pydroid.arch.BaseUiView
import com.pyamsoft.pydroid.arch.UiRender
import com.pyamsoft.pydroid.bootstrap.libraries.OssLibrary
import com.pyamsoft.pydroid.ui.databinding.AboutLibrariesListBinding
import com.pyamsoft.pydroid.ui.internal.about.AboutViewEvent.OpenLibrary
import com.pyamsoft.pydroid.ui.internal.about.AboutViewEvent.OpenLicense
import com.pyamsoft.pydroid.ui.internal.about.listitem.AboutAdapter
import com.pyamsoft.pydroid.ui.internal.about.listitem.AboutItemViewEvent.OpenLibraryUrl
import com.pyamsoft.pydroid.ui.internal.about.listitem.AboutItemViewEvent.OpenLicenseUrl
import com.pyamsoft.pydroid.ui.internal.about.listitem.AboutItemViewState
import com.pyamsoft.pydroid.ui.util.Snackbreak
import com.pyamsoft.pydroid.ui.util.removeAllItemDecorations
import com.pyamsoft.pydroid.util.asDp
import io.cabriole.decorator.LinearMarginDecoration
import me.zhanghai.android.fastscroll.FastScrollerBuilder

internal class AboutListView internal constructor(
    private val owner: LifecycleOwner,
    parent: ViewGroup
) : BaseUiView<AboutViewState, AboutViewEvent, AboutLibrariesListBinding>(parent) {

    override val viewBinding = AboutLibrariesListBinding::inflate

    override val layoutRoot by boundView { aboutList }

    private var aboutAdapter: AboutAdapter? = null

    private var lastViewed: Int = 0

    init {
        doOnInflate {
            setupListView()
        }

        doOnInflate { savedInstanceState ->
            lastViewed = savedInstanceState.getOrDefault(KEY_CURRENT, 0)
        }

        doOnTeardown {
            binding.aboutList.adapter = null
            aboutAdapter = null
        }

        doOnInflate {
            val margin = 8.asDp(binding.aboutList.context)
            LinearMarginDecoration.create(margin = margin).apply {
                binding.aboutList.addItemDecoration(this)
            }
        }

        doOnTeardown {
            binding.aboutList.removeAllItemDecorations()
        }

        doOnSaveState { outState ->
            outState.put(KEY_CURRENT, getCurrentPosition())
        }
    }

    @CheckResult
    private fun getCurrentPosition(): Int {
        val manager = binding.aboutList.layoutManager
        return if (manager is LinearLayoutManager) manager.findFirstVisibleItemPosition() else 0
    }

    private fun setupListView() {
        aboutAdapter = AboutAdapter { event, index ->
            return@AboutAdapter when (event) {
                is OpenLicenseUrl -> publish(OpenLicense(index))
                is OpenLibraryUrl -> publish(OpenLibrary(index))
            }
        }

        binding.aboutList.apply {
            adapter = aboutAdapter
            layoutManager = LinearLayoutManager(context).apply {
                initialPrefetchItemCount = 3
                isItemPrefetchEnabled = false
            }
        }

        FastScrollerBuilder(binding.aboutList)
            .useMd2Style()
            .setPopupTextProvider(aboutAdapter)
            .build()
    }

    override fun onRender(state: UiRender<AboutViewState>) {
        state.distinctBy { it.isLoading }.render(viewScope) { handleLoading(it) }
        state.distinctBy { it.loadError }.render(viewScope) { handleLoadError(it) }
        state.distinctBy { it.navigationError }.render(viewScope) { handleNavigateError(it) }
        state.distinctBy { it.licenses }.render(viewScope) { handleLicenses(it) }
    }

    private fun handleLoadError(throwable: Throwable?) {
        if (throwable != null) {
            showLoadError(throwable)
        }
    }

    private fun handleNavigateError(throwable: Throwable?) {
        if (throwable != null) {
            showNavigationError(throwable)
        }
    }

    private fun handleLoading(loading: Boolean) {
        if (loading) {
            hide()
        } else {
            show()
        }
    }

    private fun handleLicenses(licenses: List<OssLibrary>) {
        val beganEmpty = isEmpty()
        if (licenses.isEmpty()) {
            clearLicenses()
        } else {
            loadLicenses(licenses)
        }

        if (beganEmpty && !isEmpty()) {
            scrollToLastViewedItem()
        }
    }

    private fun scrollToLastViewedItem() {
        val viewed = lastViewed
        if (viewed > 0) {
            lastViewed = 0
            binding.aboutList.scrollToPosition(viewed)
        }
    }

    private fun show() {
        layoutRoot.isVisible = true
    }

    private fun hide() {
        layoutRoot.isVisible = false
    }

    @CheckResult
    private fun isEmpty(): Boolean {
        return requireNotNull(aboutAdapter).itemCount == 0
    }

    private fun loadLicenses(libraries: List<OssLibrary>) {
        requireNotNull(aboutAdapter).submitList(libraries.map { AboutItemViewState(it) })
    }

    private fun showNavigationError(error: Throwable) {
        Snackbreak.bindTo(owner) {
            long(layoutRoot, error.message ?: "An unexpected error occurred.")
        }
    }

    private fun showLoadError(error: Throwable) {
        Snackbreak.bindTo(owner) {
            long(layoutRoot, error.message ?: "An unexpected error occurred.")
        }
    }

    private fun clearLicenses() {
        requireNotNull(aboutAdapter).submitList(null)
    }

    companion object {

        private const val KEY_CURRENT = "key_current_license"
    }
}
