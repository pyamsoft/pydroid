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
import com.pyamsoft.pydroid.arch.BaseUiView
import com.pyamsoft.pydroid.arch.UiToggleView
import com.pyamsoft.pydroid.bootstrap.libraries.OssLibrary
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.about.AboutListView.Callback
import com.pyamsoft.pydroid.ui.about.listitem.AboutAdapter
import com.pyamsoft.pydroid.ui.about.listitem.AboutViewHolderUiComponent
import com.pyamsoft.pydroid.ui.util.Snackbreak

internal class AboutListView internal constructor(
  private val owner: LifecycleOwner,
  parent: ViewGroup,
  callback: Callback
) : BaseUiView<Callback>(parent, callback), UiToggleView {

  private lateinit var aboutAdapter: AboutAdapter
  private var lastViewedItem: Int = 0

  override val layout: Int = R.layout.about_libraries_list

  override val layoutRoot by lazyView<RecyclerView>(R.id.about_list)

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
    aboutAdapter.clear()
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
    aboutAdapter = AboutAdapter(object : AboutViewHolderUiComponent.Callback {

      override fun showLicense(
        name: String,
        licenseUrl: String
      ) {
        callback.onViewLicenseClicked(name, licenseUrl)
      }

      override fun showHomepage(
        name: String,
        homepageUrl: String
      ) {
        callback.onVisitHomepageClicked(name, homepageUrl)
      }

    })

    layoutRoot.apply {
      adapter = aboutAdapter
      layoutManager = LinearLayoutManager(context).apply {
        initialPrefetchItemCount = 3
        isItemPrefetchEnabled = false
      }
    }
  }

  override fun show() {
    layoutRoot.isVisible = true

    val lastViewed = lastViewedItem
    lastViewedItem = 0
    if (lastViewed > 0) {
      layoutRoot.scrollToPosition(lastViewed)
    }
  }

  override fun hide() {
    layoutRoot.isVisible = false
  }

  fun loadLicenses(libraries: List<OssLibrary>) {
    aboutAdapter.addAll(libraries)
  }

  fun showError(error: Throwable) {
    Snackbreak.bindTo(owner)
        .short(layoutRoot, error.message ?: "An unexpected error occurred.")
        .show()
  }

  interface Callback {

    fun onViewLicenseClicked(
      name: String,
      licenseUrl: String
    )

    fun onVisitHomepageClicked(
      name: String,
      homepageUrl: String
    )
  }

  companion object {

    private const val KEY_CURRENT = "key_current_license"
  }

}
