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
import androidx.recyclerview.widget.LinearLayoutManager
import com.pyamsoft.pydroid.bootstrap.libraries.OssLibrary
import com.pyamsoft.pydroid.ui.about.listitem.AboutAdapter
import com.pyamsoft.pydroid.ui.arch.UiToggleView
import com.pyamsoft.pydroid.ui.databinding.AboutLibrariesListBinding
import com.pyamsoft.pydroid.ui.util.Snackbreak

class AboutListView internal constructor(
  private val parent: ViewGroup
) : UiToggleView {

  private lateinit var binding: AboutLibrariesListBinding
  private lateinit var aboutAdapter: AboutAdapter
  private var lastViewedItem: Int = 0

  override fun id(): Int {
    return View.NO_ID
  }

  override fun inflate(savedInstanceState: Bundle?) {
    binding = AboutLibrariesListBinding.inflate(parent.inflater(), parent, false)
    parent.addView(binding.root)

    restoreLastViewedItem(savedInstanceState)
    setupListView()
  }

  internal fun teardown() {
    binding.aboutList.adapter = null
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
    val manager = binding.aboutList.layoutManager
    if (manager is LinearLayoutManager) {
      return manager.findFirstVisibleItemPosition()
    } else {
      return 0
    }
  }

  private fun setupListView() {
    aboutAdapter = AboutAdapter()
    binding.apply {
      aboutList.adapter = aboutAdapter
      aboutList.layoutManager = LinearLayoutManager(aboutList.context).apply {
        initialPrefetchItemCount = 3
        isItemPrefetchEnabled = false
      }
    }
  }

  override fun show() {
    binding.aboutList.isVisible = true

    val lastViewed = lastViewedItem
    lastViewedItem = 0
    if (lastViewed > 0) {
      binding.aboutList.scrollToPosition(lastViewed)
    }
  }

  override fun hide() {
    binding.aboutList.isVisible = false
  }

  fun loadLicenses(libraries: List<OssLibrary>) {
    aboutAdapter.addAll(libraries)
  }

  fun showError(error: Throwable) {
    Snackbreak.short(parent, error.localizedMessage)
        .show()
  }

  companion object {

    private const val KEY_CURRENT = "key_current_license"
  }

}