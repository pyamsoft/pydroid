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
 *
 */

package com.pyamsoft.pydroid.ui.about;

import android.graphics.Color;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.mikepenz.fastadapter.items.GenericAbstractItem;
import com.pyamsoft.pydroid.about.AboutLibrariesModel;
import com.pyamsoft.pydroid.helper.Checker;
import com.pyamsoft.pydroid.loader.ImageLoader;
import com.pyamsoft.pydroid.loader.LoaderHelper;
import com.pyamsoft.pydroid.loader.loaded.Loaded;
import com.pyamsoft.pydroid.ui.R;
import com.pyamsoft.pydroid.ui.databinding.AdapterItemAboutBinding;
import com.pyamsoft.pydroid.util.NetworkUtil;
import java.util.List;

class AboutLibrariesItem extends
    GenericAbstractItem<AboutLibrariesModel, AboutLibrariesItem, AboutLibrariesItem.ViewHolder> {

  @SuppressWarnings("WeakerAccess") @NonNull Loaded arrowLoad = LoaderHelper.empty();
  @SuppressWarnings("WeakerAccess") @Nullable ViewPropertyAnimatorCompat arrowAnimation;
  @SuppressWarnings("WeakerAccess") boolean expanded;

  AboutLibrariesItem(@NonNull AboutLibrariesModel item) {
    super(Checker.checkNonNull(item));
  }

  @CheckResult @Override public int getType() {
    return R.id.fastadapter_expandable_about_item;
  }

  @CheckResult @Override public int getLayoutRes() {
    return R.layout.adapter_item_about;
  }

  @SuppressWarnings("WeakerAccess") void cancelArrowAnimation() {
    if (arrowAnimation != null) {
      arrowAnimation.cancel();
      arrowAnimation = null;
    }
  }

  @Override public void bindView(@NonNull final ViewHolder holder, List<Object> payloads) {
    super.bindView(holder, payloads);

    arrowLoad = LoaderHelper.unload(arrowLoad);
    arrowLoad = ImageLoader.fromResource(holder.itemView.getContext(), R.drawable.ic_arrow_up_24dp)
        .into(holder.binding.expandLicenseIcon);

    cancelArrowAnimation();
    if (expanded) {
      holder.binding.expandLicenseText.setVisibility(View.VISIBLE);
      ViewCompat.setRotation(holder.binding.expandLicenseIcon, 0);
    } else {
      holder.binding.expandLicenseText.setVisibility(View.GONE);
      ViewCompat.setRotation(holder.binding.expandLicenseIcon, 180);
    }

    holder.binding.expandLicenseText.getSettings().setTextZoom(80);
    holder.binding.expandLicenseName.setText(getModel().name());
    holder.binding.expandLicenseHomepage.setOnClickListener(
        view -> NetworkUtil.newLink(view.getContext().getApplicationContext(),
            getModel().homepage()));
    holder.binding.expandLicenseText.loadDataWithBaseURL(null, getModel().license(), "text/plain",
        "UTF-8", null);

    holder.itemView.setOnClickListener(v -> {
      expanded = !expanded;
      cancelArrowAnimation();
      arrowAnimation =
          ViewCompat.animate(holder.binding.expandLicenseIcon).rotation(expanded ? 0 : 180);
      holder.binding.expandLicenseText.setVisibility(expanded ? View.VISIBLE : View.GONE);
    });
  }

  @Override public void unbindView(ViewHolder holder) {
    super.unbindView(holder);
    holder.binding.expandLicenseHomepage.setOnClickListener(null);
    holder.binding.expandLicenseName.setText(null);
    holder.binding.expandLicenseIcon.setImageDrawable(null);
    holder.binding.expandLicenseText.loadDataWithBaseURL(null, "", "text/plain", "UTF-8", null);
    holder.itemView.setOnClickListener(null);
    cancelArrowAnimation();
  }

  @Override public ViewHolder getViewHolder(View view) {
    return new ViewHolder(view);
  }

  static class ViewHolder extends RecyclerView.ViewHolder {

    @NonNull final AdapterItemAboutBinding binding;

    ViewHolder(View view) {
      super(view);
      binding = AdapterItemAboutBinding.bind(view);
      binding.expandLicenseHomepage.setTextColor(Color.BLUE);
      binding.expandLicenseHomepage.setSingleLine(true);
      binding.expandLicenseText.getSettings().setDefaultFontSize(12);
    }
  }
}
