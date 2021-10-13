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
import com.pyamsoft.pydroid.arch.UiRender
import com.pyamsoft.pydroid.bootstrap.otherapps.api.OtherApp
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.ui.databinding.OtherAppsListBinding
import com.pyamsoft.pydroid.ui.internal.otherapps.listitem.OtherAppsAdapter
import com.pyamsoft.pydroid.ui.internal.otherapps.listitem.OtherAppsItemViewState
import com.pyamsoft.pydroid.ui.util.removeAllItemDecorations
import com.pyamsoft.pydroid.util.asDp
import io.cabriole.decorator.LinearMarginDecoration
import me.zhanghai.android.fastscroll.FastScrollerBuilder

internal class OtherAppsList internal constructor(owner: LifecycleOwner, parent: ViewGroup) :
    BaseUiView<OtherAppsViewState, OtherAppsViewEvent.ListEvent, OtherAppsListBinding>(parent),
    OtherAppsAdapter.Callback {

  override val viewBinding = OtherAppsListBinding::inflate

  override val layoutRoot by boundView { otherAppsList }

  private var listAdapter: OtherAppsAdapter? = null

  private var lastViewed: Int = 0

  init {
    doOnInflate { setupListView(owner) }

    doOnInflate { savedInstanceState -> lastViewed = savedInstanceState.get(KEY_CURRENT) ?: 0 }

    doOnTeardown {
      binding.otherAppsList.adapter = null
      listAdapter = null
    }

    doOnInflate {
      val margin = 8.asDp(binding.otherAppsList.context)
      LinearMarginDecoration.create(margin = margin).apply {
        binding.otherAppsList.addItemDecoration(this)
      }
    }

    doOnTeardown { binding.otherAppsList.removeAllItemDecorations() }

    doOnSaveState { outState -> outState.put(KEY_CURRENT, getCurrentPosition()) }
  }

  @CheckResult
  private fun getCurrentPosition(): Int {
    val manager = binding.otherAppsList.layoutManager
    return if (manager is LinearLayoutManager) manager.findFirstVisibleItemPosition() else 0
  }

  private fun setupListView(owner: LifecycleOwner) {
    listAdapter = OtherAppsAdapter(owner, this)

    binding.otherAppsList.apply {
      adapter = listAdapter
      layoutManager =
          LinearLayoutManager(context).apply {
            initialPrefetchItemCount = 3
            isItemPrefetchEnabled = false
          }
    }

    FastScrollerBuilder(binding.otherAppsList)
        .useMd2Style()
        .setPopupTextProvider(listAdapter)
        .build()
  }

  override fun onRender(state: UiRender<OtherAppsViewState>) {
    state.mapChanged { it.apps }.render(viewScope) { handleApps(it) }
  }

  private fun handleApps(apps: List<OtherApp>) {
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

  private fun scrollToLastViewedItem() {
    val viewed = lastViewed
    if (viewed > 0) {
      lastViewed = 0
      binding.otherAppsList.scrollToPosition(viewed)
    }
  }

  @CheckResult
  private fun usingAdapter(): OtherAppsAdapter {
    return listAdapter.requireNotNull()
  }

  @CheckResult
  private fun isEmpty(): Boolean {
    return usingAdapter().itemCount == 0
  }

  private fun loadApps(apps: List<OtherApp>) {
    usingAdapter().submitList(apps.map { OtherAppsItemViewState(it) })
  }

  private fun clearApps() {
    usingAdapter().submitList(null)
  }

  override fun onOpenStore(index: Int) {
    publish(OtherAppsViewEvent.ListEvent.OpenStore(index))
  }

  override fun onViewSource(index: Int) {
    publish(OtherAppsViewEvent.ListEvent.ViewSource(index))
  }

  companion object {

    private const val KEY_CURRENT = "key_current_app"
  }
}
