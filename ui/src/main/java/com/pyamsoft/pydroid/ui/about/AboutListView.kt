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

package com.pyamsoft.pydroid.ui.about

import android.view.ViewGroup
import androidx.annotation.CheckResult
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.pyamsoft.pydroid.arch.BaseUiView
import com.pyamsoft.pydroid.bootstrap.libraries.OssLibrary
import com.pyamsoft.pydroid.ui.about.AboutViewEvent.OpenLibrary
import com.pyamsoft.pydroid.ui.about.AboutViewEvent.OpenLicense
import com.pyamsoft.pydroid.ui.about.listitem.AboutAdapter
import com.pyamsoft.pydroid.ui.about.listitem.AboutItemViewEvent.OpenLibraryUrl
import com.pyamsoft.pydroid.ui.about.listitem.AboutItemViewEvent.OpenLicenseUrl
import com.pyamsoft.pydroid.ui.about.listitem.AboutItemViewState
import com.pyamsoft.pydroid.ui.databinding.AboutLibrariesListBinding
import com.pyamsoft.pydroid.ui.util.Snackbreak
import com.pyamsoft.pydroid.util.toDp
import io.cabriole.decorator.LinearMarginDecoration

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
            clearLicenses()
            aboutAdapter = null
        }

        doOnInflate {
            val margin = 8.toDp(binding.aboutList.context)
            LinearMarginDecoration.create(margin = margin).apply {
                binding.aboutList.addItemDecoration(this)
                doOnTeardown { binding.aboutList.removeItemDecoration(this) }
            }
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
        aboutAdapter = AboutAdapter(owner) { event, index ->
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
    }

    override fun onRender(state: AboutViewState) {
        handleLicenses(state)
        handleLoading(state)
        handleNavigateError(state)
        handleLoadError(state)
    }

    private fun handleLoadError(state: AboutViewState) {
        state.loadError.let { throwable ->
            if (throwable == null) {
                clearLoadError()
            } else {
                showLoadError(throwable)
            }
        }
    }

    private fun handleNavigateError(state: AboutViewState) {
        state.navigationError.let { throwable ->
            if (throwable == null) {
                clearNavigationError()
            } else {
                showNavigationError(throwable)
            }
        }
    }

    private fun handleLoading(state: AboutViewState) {
        state.isLoading.let { loading ->
            if (loading) {
                hide()
            } else {
                show()
            }
        }
    }

    private fun handleLicenses(state: AboutViewState) {
        state.licenses.let { licenses ->
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
        Snackbreak.bindTo(owner, "navigate") {
            make(layoutRoot, error.message ?: "An unexpected error occurred.")
        }
    }

    private fun clearNavigationError() {
        Snackbreak.bindTo(owner, "navigate") {
            dismiss()
        }
    }

    private fun showLoadError(error: Throwable) {
        Snackbreak.bindTo(owner, "load") {
            make(layoutRoot, error.message ?: "An unexpected error occurred.")
        }
    }

    private fun clearLoadError() {
        Snackbreak.bindTo(owner, "load") {
            dismiss()
        }
    }

    private fun clearLicenses() {
        requireNotNull(aboutAdapter).submitList(null)
    }

    companion object {

        private const val KEY_CURRENT = "key_current_license"
    }
}
