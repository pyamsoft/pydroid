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

package com.pyamsoft.pydroid.about;

import android.graphics.Color;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;
import com.pyamsoft.pydroid.ActionSingle;
import com.pyamsoft.pydroid.R;
import com.pyamsoft.pydroid.databinding.AdapterItemAboutBinding;
import com.pyamsoft.pydroid.util.NetworkUtil;
import java.util.List;
import timber.log.Timber;

class AboutAdapterItem extends AbstractItem<AboutAdapterItem, AboutAdapterItem.ViewHolder> {

  @NonNull private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();
  @NonNull private final AboutLicenseItem item;
  @NonNull private String licenseText;
  private boolean expanded;

  @NonNull private final FastAdapter.OnClickListener<AboutAdapterItem> onClickListener =
      (v, adapter, item, position) -> {
        item.switchExpanded();
        adapter.getFastAdapter().notifyItemChanged(position);
        return true;
      };

  AboutAdapterItem(@NonNull AboutLicenseItem item) {
    this.item = item;
    licenseText = "";
  }

  void setLicenseText(@NonNull String licenseText) {
    this.licenseText = licenseText;
  }

  @SuppressWarnings("WeakerAccess") void switchExpanded() {
    this.expanded = !expanded;
  }

  @CheckResult @NonNull @Override
  public FastAdapter.OnClickListener<AboutAdapterItem> getOnItemClickListener() {
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

  @Override public void bindView(@NonNull ViewHolder viewHolder, List<Object> payloads) {
    super.bindView(viewHolder, payloads);
    viewHolder.bind(expanded, licenseText, item);
  }

  @Override public void unbindView(ViewHolder holder) {
    super.unbindView(holder);
    holder.unbind();
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

    @NonNull private final AdapterItemAboutBinding binding;
    private boolean expanded;
    @NonNull private String license;
    @Nullable private AboutLicenseItem item;

    public ViewHolder(View view) {
      super(view);
      binding = AdapterItemAboutBinding.bind(view);
      binding.expandLicenseText.getSettings().setTextZoom(80);
      binding.expandLicenseProgress.setIndeterminate(true);
      binding.expandLicenseHomepage.setTextColor(Color.BLUE);
      binding.expandLicenseHomepage.setSingleLine(true);
      expanded = false;
      license = "";
    }

    void bind(boolean isExpanded, @NonNull String licenseText, @NonNull AboutLicenseItem item) {
      //make sure all animations are stopped
      binding.expandLicenseIcon.clearAnimation();
      if (isExpanded) {
        ViewCompat.setRotation(binding.expandLicenseIcon, 0);
      } else {
        ViewCompat.setRotation(binding.expandLicenseIcon, 180);
      }

      binding.expandLicenseName.setText(item.name());
      binding.expandLicenseHomepage.setOnClickListener(
          view -> NetworkUtil.newLink(view.getContext().getApplicationContext(), item.homepage()));

      if (isExpanded) {
        if (licenseText.length() == 0) {
          binding.expandLicenseProgress.setVisibility(View.VISIBLE);
        } else {
          binding.expandLicenseProgress.setVisibility(View.GONE);
          binding.expandLicenseText.setVisibility(View.VISIBLE);
          binding.expandLicenseText.loadDataWithBaseURL(null, licenseText, "text/plain", "UTF-8",
              null);
        }
      } else {
        binding.expandLicenseProgress.setVisibility(View.GONE);
        binding.expandLicenseText.setVisibility(View.GONE);
        binding.expandLicenseText.loadDataWithBaseURL(null, "", "text/plain", "UTF-8", null);
      }

      this.item = item;
      expanded = isExpanded;
      license = licenseText;
    }

    void bind(@NonNull ActionSingle<AboutLicenseItem> loader) {
      if (expanded) {
        if (license.length() == 0) {
          if (item != null) {
            loader.call(item);
          } else {
            Timber.e("AboutLicenseItem is NULL");
          }
        }
      }
    }

    void unbind() {
      binding.expandLicenseHomepage.setOnClickListener(null);
      binding.expandLicenseName.setText(null);
      item = null;
    }
  }
}
