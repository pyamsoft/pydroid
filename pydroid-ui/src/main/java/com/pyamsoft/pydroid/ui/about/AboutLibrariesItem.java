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

package com.pyamsoft.pydroid.ui.about;

import android.graphics.Color;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.webkit.WebView;
import com.mikepenz.fastadapter.items.GenericAbstractItem;
import com.pyamsoft.pydroid.about.AboutLibrariesModel;
import com.pyamsoft.pydroid.helper.Checker;
import com.pyamsoft.pydroid.ui.R;
import com.pyamsoft.pydroid.ui.databinding.AdapterItemAboutBinding;
import com.pyamsoft.pydroid.util.NetworkUtil;
import java.util.List;

class AboutLibrariesItem extends
    GenericAbstractItem<AboutLibrariesModel, AboutLibrariesItem, AboutLibrariesItem.ViewHolder> {

  AboutLibrariesItem(@NonNull AboutLibrariesModel item) {
    super(Checker.Companion.checkNonNull(item));
  }

  @CheckResult @Override public int getType() {
    return R.id.fastadapter_expandable_about_item;
  }

  @CheckResult @Override public int getLayoutRes() {
    return R.layout.adapter_item_about;
  }

  @Override public void bindView(@NonNull final ViewHolder holder, List<Object> payloads) {
    super.bindView(holder, payloads);
    holder.binding.aboutExpander.setTitle(getModel().name());
    holder.binding.aboutExpander.editDescriptionView()
        .setOnClickListener(v -> NetworkUtil.Companion.newLink(v.getContext(), getModel().homepage()));
    holder.webView.loadDataWithBaseURL(null, getModel().license(), "text/plain", "UTF-8", null);
  }

  @Override public void unbindView(ViewHolder holder) {
    super.unbindView(holder);
    holder.binding.aboutExpander.clearTitle();
    holder.binding.aboutExpander.editDescriptionView().setOnClickListener(null);
    holder.webView.loadDataWithBaseURL(null, null, "text/plain", "UTF-8", null);
  }

  @Override public ViewHolder getViewHolder(View view) {
    return new ViewHolder(view);
  }

  static class ViewHolder extends RecyclerView.ViewHolder {

    @NonNull final AdapterItemAboutBinding binding;
    @NonNull final WebView webView;

    ViewHolder(View view) {
      super(view);
      binding = AdapterItemAboutBinding.bind(view);
      webView = new WebView(view.getContext());

      binding.aboutExpander.setTitleTextSize(16);
      binding.aboutExpander.setDescription("Homepage");
      binding.aboutExpander.editDescriptionView().setTextColor(Color.BLUE);
      binding.aboutExpander.editDescriptionView().setSingleLine(true);

      webView.getSettings().setDefaultFontSize(12);
      binding.aboutExpander.setExpandingContent(webView);
    }
  }
}
