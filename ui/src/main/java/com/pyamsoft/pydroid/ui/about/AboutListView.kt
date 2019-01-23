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
import android.view.ViewGroup
import androidx.annotation.CheckResult
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pyamsoft.pydroid.bootstrap.libraries.OssLibrary
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.about.listitem.AboutAdapter
import com.pyamsoft.pydroid.ui.arch.UiToggleView
import com.pyamsoft.pydroid.ui.arch.UiView
import com.pyamsoft.pydroid.ui.arch.ViewEvent.EMPTY
import com.pyamsoft.pydroid.ui.arch.ViewEvent.EmptyPublisher
import com.pyamsoft.pydroid.ui.util.Snackbreak

class AboutListView internal constructor(
  private val owner: LifecycleOwner,
  private val parent: ViewGroup
) : UiView<EMPTY>(EmptyPublisher), UiToggleView<EMPTY> {

  private lateinit var aboutList: RecyclerView

  private lateinit var aboutAdapter: AboutAdapter
  private var lastViewedItem: Int = 0

  override fun id(): Int {
    return aboutList.id
  }

  override fun inflate(savedInstanceState: Bundle?) {
    parent.inflateAndAdd(R.layout.about_libraries_list) {
      aboutList = findViewById(R.id.about_list)
    }

    restoreLastViewedItem(savedInstanceState)
    setupListView()
  }

  override fun teardown() {
    aboutList.adapter = null
    aboutAdapter.clear()
  }

  override fun saveState(outState: Bundle) {
    outState.putInt(KEY_CURRENT, getCurrentPosition())
  }

  private fun restoreLastViewedItem(savedInstanceState: Bundle?) {
    lastViewedItem = savedInstanceState?.getInt(KEY_CURRENT) ?: 0
  }

  @CheckResult
  private fun getCurrentPosition(): Int {
    val manager = aboutList.layoutManager
    if (manager is LinearLayoutManager) {
      return manager.findFirstVisibleItemPosition()
    } else {
      return 0
    }
  }

  private fun setupListView() {
    aboutAdapter = AboutAdapter(owner)

    aboutList.apply {
      adapter = aboutAdapter
      layoutManager = LinearLayoutManager(context).apply {
        initialPrefetchItemCount = 3
        isItemPrefetchEnabled = false
      }
    }
  }

  override fun show() {
    aboutList.isVisible = true

    val lastViewed = lastViewedItem
    lastViewedItem = 0
    if (lastViewed > 0) {
      aboutList.scrollToPosition(lastViewed)
    }
  }

  override fun hide() {
    aboutList.isVisible = false
  }

  fun loadLicenses(libraries: List<OssLibrary>) {
    aboutAdapter.addAll(libraries)
  }

  fun showError(error: Throwable) {
    Snackbreak.bindTo(owner)
        .short(parent, error.message ?: "An unexpected error occurred.")
        .show()
  }

  companion object {

    private const val KEY_CURRENT = "key_current_license"
  }

}