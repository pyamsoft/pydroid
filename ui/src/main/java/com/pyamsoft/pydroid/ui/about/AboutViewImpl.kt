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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CheckResult
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle.Event.ON_DESTROY
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import androidx.recyclerview.widget.LinearLayoutManager
import com.pyamsoft.pydroid.bootstrap.libraries.OssLibrary
import com.pyamsoft.pydroid.ui.databinding.FragmentAboutLibrariesBinding
import com.pyamsoft.pydroid.ui.util.Snackbreak
import com.pyamsoft.pydroid.ui.widget.RefreshLatch

internal class AboutViewImpl internal constructor(
  private val inflater: LayoutInflater,
  private val container: ViewGroup?,
  private val savedInstanceState: Bundle?,
  private val activity: FragmentActivity,
  private val owner: LifecycleOwner
) : AboutView, LifecycleObserver {

  private lateinit var binding: FragmentAboutLibrariesBinding
  private lateinit var adapter: AboutAdapter
  private lateinit var refreshLatch: RefreshLatch
  private var lastViewedItem: Int = 0

  init {
    owner.lifecycle.addObserver(this)
  }

  @Suppress("unused")
  @OnLifecycleEvent(ON_DESTROY)
  internal fun destroy() {
    owner.lifecycle.removeObserver(this)

    binding.apply {
      aboutList.adapter = null
      aboutList.clearOnScrollListeners()
      adapter.clear()
      unbind()
    }
  }

  override fun create() {
    binding = FragmentAboutLibrariesBinding.inflate(inflater, container, false)
    restoreLastViewedItem(savedInstanceState)
    setupRefreshLatch()
    setupAboutList()
  }

  override fun root(): View {
    return binding.root
  }

  private fun setupRefreshLatch() {
    refreshLatch = RefreshLatch.create(owner, delay = 150L) {
      binding.apply {
        if (it) {
          progressSpinner.visibility = View.VISIBLE
          aboutList.visibility = View.INVISIBLE
        } else {
          // Load complete
          progressSpinner.visibility = View.GONE
          aboutList.visibility = View.VISIBLE

          val lastViewed = lastViewedItem
          aboutList.scrollToPosition(lastViewed)
        }
      }
    }
  }

  private fun restoreLastViewedItem(savedInstanceState: Bundle?) {
    lastViewedItem = savedInstanceState?.getInt(KEY_PAGE) ?: 0
  }

  private fun setupAboutList() {
    adapter = AboutAdapter(activity)
    binding.apply {
      aboutList.adapter = adapter
      aboutList.layoutManager = LinearLayoutManager(activity).apply {
        initialPrefetchItemCount = 3
        isItemPrefetchEnabled = false
      }
    }
    refreshLatch.isRefreshing = true
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

  override fun saveInstanceState(outState: Bundle) {
    outState.putInt(KEY_PAGE, getCurrentPosition())
  }

  override fun onLoadBegin(forced: Boolean) {
    refreshLatch.isRefreshing = true
  }

  override fun onLoadSuccess(libraries: List<OssLibrary>) {
    adapter.addAll(libraries)
  }

  override fun onLoadError(throwable: Throwable) {
    Snackbreak.short(root(), throwable.localizedMessage)
        .show()
  }

  override fun onLoadComplete() {
    refreshLatch.isRefreshing = false
  }

  companion object {

    private const val KEY_PAGE = "key_current_page"
  }

}