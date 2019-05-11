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

package com.pyamsoft.pydroid.ui.about

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CheckResult
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pyamsoft.pydroid.arch.UiViewImpl
import com.pyamsoft.pydroid.arch.onChange
import com.pyamsoft.pydroid.bootstrap.libraries.OssLibrary
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.about.AboutListViewEvent.OpenUrl
import com.pyamsoft.pydroid.ui.about.listitem.AboutAdapter
import com.pyamsoft.pydroid.ui.about.listitem.AboutItemControllerEvent
import com.pyamsoft.pydroid.ui.util.Snackbreak

internal class AboutListView internal constructor(
  private val owner: LifecycleOwner,
  parent: ViewGroup
) : UiViewImpl<AboutListState, AboutListViewEvent>(parent) {

  private var lastViewedItem: Int = 0
  private var aboutAdapter: AboutAdapter? = null

  override val layout: Int = R.layout.about_libraries_list

  override val layoutRoot by boundView<RecyclerView>(R.id.about_list)

  override fun onInflated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    restoreLastViewedItem(savedInstanceState)
    setupListView()
  }

  override fun onTeardown() {
    super.onTeardown()
    layoutRoot.adapter = null
    clearLicenses()
    clearError()
    aboutAdapter = null
  }

  override fun onSaveState(outState: Bundle) {
    outState.putInt(KEY_CURRENT, getCurrentPosition())
  }

  private fun restoreLastViewedItem(savedInstanceState: Bundle?) {
    lastViewedItem = savedInstanceState?.getInt(KEY_CURRENT) ?: 0
  }

  @CheckResult
  private fun getCurrentPosition(): Int {
    val manager = layoutRoot.layoutManager
    if (manager is LinearLayoutManager) {
      return manager.findFirstVisibleItemPosition()
    } else {
      return 0
    }
  }

  private fun setupListView() {
    aboutAdapter = AboutAdapter {
      return@AboutAdapter when (it) {
        is AboutItemControllerEvent.ExternalUrl -> publish(OpenUrl(it.url))
      }
    }

    layoutRoot.apply {
      adapter = aboutAdapter
      layoutManager = LinearLayoutManager(context).apply {
        initialPrefetchItemCount = 3
        isItemPrefetchEnabled = false
      }
    }
  }

  override fun onRender(
    state: AboutListState,
    oldState: AboutListState?
  ) {
    state.onChange(oldState, field = { it.isLoading }) { loading ->
      if (loading) {
        hide()
      } else {
        show()
      }
    }

    state.onChange(oldState, field = { it.licenses }) { licenses ->
      if (licenses.isEmpty()) {
        clearLicenses()
      } else {
        loadLicenses(licenses)
      }
    }

    state.onChange(oldState, field = { it.throwable }) { throwable ->
      if (throwable == null) {
        clearError()
      } else {
        showError(throwable)
      }
    }
  }

  private fun show() {
    layoutRoot.isVisible = true

    val lastViewed = lastViewedItem
    lastViewedItem = 0
    if (lastViewed > 0) {
      layoutRoot.scrollToPosition(lastViewed)
    }
  }

  private fun hide() {
    layoutRoot.isVisible = false
  }

  private fun loadLicenses(libraries: List<OssLibrary>) {
    requireNotNull(aboutAdapter).submitList(libraries)
  }

  private fun showError(error: Throwable) {
    Snackbreak.bindTo(owner)
        .short(layoutRoot, error.message ?: "An unexpected error occurred.")
        .show()
  }

  private fun clearError() {
    Snackbreak.bindTo(owner)
        .dismiss()
  }

  private fun clearLicenses() {
    requireNotNull(aboutAdapter).submitList(null)
  }

  companion object {

    private const val KEY_CURRENT = "key_current_license"
  }

}
