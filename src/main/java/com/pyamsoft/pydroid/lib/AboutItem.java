/*
 * Copyright 2016 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.lib;

import android.graphics.Color;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;
import com.pyamsoft.pydroid.R;
import com.pyamsoft.pydroid.R2;
import com.pyamsoft.pydroid.util.NetworkUtil;
import java.util.List;

class AboutItem extends AbstractItem<AboutItem, AboutItem.ViewHolder> {

  @NonNull private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();
  @SuppressWarnings("WeakerAccess") @NonNull final String licenseHomepage;
  @NonNull private final String licenseName;
  @NonNull private final Licenses licenseType;
  @NonNull private String licenseText;
  private boolean expanded;

  @NonNull private final FastAdapter.OnClickListener<AboutItem> onClickListener =
      (v, adapter, item, position) -> {
        item.setExpanded(!item.isExpanded());
        adapter.getFastAdapter().notifyItemChanged(position);
        return true;
      };

  AboutItem(@NonNull String licenseName, @NonNull String licenseHomepage,
      @NonNull Licenses licenseType) {
    this.licenseName = licenseName;
    this.licenseHomepage = licenseHomepage;
    this.licenseType = licenseType;
    licenseText = "";
  }

  void setLicenseText(@NonNull String licenseText) {
    this.licenseText = licenseText;
  }

  @CheckResult @SuppressWarnings("WeakerAccess") boolean isExpanded() {
    return expanded;
  }

  @SuppressWarnings("WeakerAccess") void setExpanded(boolean expanded) {
    this.expanded = expanded;
  }

  @CheckResult @NonNull @Override
  public FastAdapter.OnClickListener<AboutItem> getOnItemClickListener() {
    return onClickListener;
  }

  @CheckResult @Override public boolean isSelectable() {
    return true;
  }

  @CheckResult @Override public int getType() {
    return R.id.fastadapter_expandable_about_item;
  }

  @CheckResult @Override public int getLayoutRes() {
    return R.layout.adapter_item_about;
  }

  @Override public void bindView(@NonNull ViewHolder viewHolder, List payloads) {
    super.bindView(viewHolder, payloads);
    viewHolder.licenseText.setClickable(false);
    viewHolder.licenseText.setFocusable(false);
    viewHolder.licenseName.setClickable(false);
    viewHolder.licenseName.setFocusable(false);
    viewHolder.progressBar.setClickable(false);
    viewHolder.progressBar.setFocusable(false);
    viewHolder.licenseHomepage.setOnClickListener(null);

    //make sure all animations are stopped
    viewHolder.arrowIcon.clearAnimation();
    if (isExpanded()) {
      ViewCompat.setRotation(viewHolder.arrowIcon, 0);
    } else {
      ViewCompat.setRotation(viewHolder.arrowIcon, 180);
    }

    viewHolder.licenseName.setText(licenseName);
    viewHolder.licenseHomepage.setTextColor(Color.BLUE);
    viewHolder.licenseHomepage.setSingleLine(true);
    viewHolder.licenseHomepage.setOnClickListener(
        view -> NetworkUtil.newLink(view.getContext().getApplicationContext(), licenseHomepage));

    if (isExpanded()) {
      if (licenseText.length() == 0) {
        viewHolder.progressBar.setVisibility(View.VISIBLE);
        AboutItemBus.get()
            .post(AboutLicenseLoadEvent.create(viewHolder.getAdapterPosition(), licenseType));
      } else {
        viewHolder.progressBar.setVisibility(View.GONE);
        viewHolder.licenseText.setVisibility(View.VISIBLE);
        viewHolder.licenseText.loadDataWithBaseURL(null, licenseText, "text/plain", "UTF-8", null);
      }
    } else {
      viewHolder.progressBar.setVisibility(View.GONE);
      viewHolder.licenseText.setVisibility(View.GONE);
      viewHolder.licenseText.loadDataWithBaseURL(null, "", "text/plain", "UTF-8", null);
    }
  }

  @CheckResult @Override public ViewHolderFactory<? extends ViewHolder> getFactory() {
    return FACTORY;
  }

  @SuppressWarnings("WeakerAccess") protected static class ItemFactory
      implements ViewHolderFactory<ViewHolder> {

    @CheckResult @Override public ViewHolder create(@NonNull View v) {
      return new ViewHolder(v);
    }
  }

  static class ViewHolder extends RecyclerView.ViewHolder {

    @NonNull final Unbinder unbinder;
    @BindView(R2.id.expand_license_name) TextView licenseName;
    @BindView(R2.id.expand_license_homepage) TextView licenseHomepage;
    @BindView(R2.id.expand_license_text) WebView licenseText;
    @BindView(R2.id.expand_license_icon) ImageView arrowIcon;
    @BindView(R2.id.expand_license_progress) ProgressBar progressBar;

    public ViewHolder(View view) {
      super(view);
      unbinder = ButterKnife.bind(this, view);

      licenseText.getSettings().setTextZoom(80);
      progressBar.setIndeterminate(true);
    }
  }
}
