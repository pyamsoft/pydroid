/*
 * Copyright 2017 Peter Kenji Yamanaka
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

import android.graphics.Color
import android.support.annotation.CheckResult
import android.support.v7.widget.RecyclerView
import android.view.View
import android.webkit.WebView
import com.mikepenz.fastadapter.items.GenericAbstractItem
import com.pyamsoft.pydroid.about.AboutLibrariesModel
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.util.NetworkUtil
import kotlinx.android.synthetic.main.adapter_item_about.view.about_expander

internal class AboutLibrariesItem(
    item: AboutLibrariesModel) : GenericAbstractItem<AboutLibrariesModel, AboutLibrariesItem, AboutLibrariesItem.ViewHolder>(
    item) {

  @CheckResult override fun getType(): Int {
    return R.id.fastadapter_expandable_about_item
  }

  @CheckResult override fun getLayoutRes(): Int {
    return R.layout.adapter_item_about
  }

  override fun bindView(holder: ViewHolder, payloads: List<Any>?) {
    super.bindView(holder, payloads)
    holder.itemView.about_expander.setTitle(model.name)
    holder.itemView.about_expander.editDescriptionView().setOnClickListener {
      NetworkUtil.newLink(it.context, model.homepage)
    }
    holder.webView.loadDataWithBaseURL(null, model.license, "text/plain", "UTF-8", null)
  }

  override fun unbindView(holder: ViewHolder?) {
    super.unbindView(holder)
    if (holder != null) {
      holder.itemView.about_expander.clearTitle()
      holder.itemView.about_expander.editDescriptionView().setOnClickListener(null)
      holder.webView.loadDataWithBaseURL(null, null, "text/plain", "UTF-8", null)
    }
  }

  override fun getViewHolder(view: View): ViewHolder {
    return ViewHolder(view)
  }

  internal class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    val webView: WebView = WebView(view.context)

    init {
      itemView.about_expander.setTitleTextSize(16)
      itemView.about_expander.setDescription("Homepage")
      itemView.about_expander.editDescriptionView().setTextColor(Color.BLUE)
      itemView.about_expander.editDescriptionView().setSingleLine(true)

      webView.settings.defaultFontSize = 12
      itemView.about_expander.setExpandingContent(webView)
    }
  }
}
