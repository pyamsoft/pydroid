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
 *
 */

package com.pyamsoft.pydroid.ui.about;

import android.graphics.Color;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.GenericAbstractItem;
import com.pyamsoft.pydroid.about.AboutLibrariesItemPresenter;
import com.pyamsoft.pydroid.about.AboutLibrariesModel;
import com.pyamsoft.pydroid.helper.Checker;
import com.pyamsoft.pydroid.ui.PYDroidInjector;
import com.pyamsoft.pydroid.ui.R;
import com.pyamsoft.pydroid.ui.databinding.AdapterItemAboutBinding;
import com.pyamsoft.pydroid.util.NetworkUtil;
import java.util.List;

class AboutLibrariesItem extends
    GenericAbstractItem<AboutLibrariesModel, AboutLibrariesItem, AboutLibrariesItem.ViewHolder> {

  AboutLibrariesItemPresenter presenter;
  @NonNull private String licenseText;
  private boolean expanded;
  @NonNull private final FastAdapter.OnClickListener<AboutLibrariesItem> onClickListener =
      (view, iAdapter, item, i) -> {
        item.switchExpanded();
        FastAdapter<AboutLibrariesItem> fastAdapter = iAdapter.getFastAdapter();
        if (fastAdapter != null) {
          fastAdapter.notifyAdapterItemChanged(i);
        }
        return false;
      };

  AboutLibrariesItem(@NonNull AboutLibrariesModel item) {
    super(Checker.checkNonNull(item));
    licenseText = "";
    PYDroidInjector.get().provideComponent().plusAboutLibrariesComponent().inject(this);
  }

  @SuppressWarnings("WeakerAccess") void setLicenseText(@NonNull String licenseText) {
    this.licenseText = Checker.checkNonNull(licenseText);
  }

  @SuppressWarnings("WeakerAccess") void switchExpanded() {
    this.expanded = !expanded;
  }

  @CheckResult @NonNull @Override
  public FastAdapter.OnClickListener<AboutLibrariesItem> getOnItemClickListener() {
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
    viewHolder = Checker.checkNonNull(viewHolder);
    super.bindView(viewHolder, payloads);
    //make sure all animations are stopped
    viewHolder.binding.expandLicenseIcon.clearAnimation();
    if (expanded) {
      ViewCompat.setRotation(viewHolder.binding.expandLicenseIcon, 0);
    } else {
      ViewCompat.setRotation(viewHolder.binding.expandLicenseIcon, 180);
    }

    viewHolder.binding.expandLicenseName.setText(getModel().name());
    viewHolder.binding.expandLicenseHomepage.setOnClickListener(
        view -> NetworkUtil.newLink(view.getContext().getApplicationContext(),
            getModel().homepage()));

    loadLicenseView(viewHolder);
  }

  @SuppressWarnings("WeakerAccess") void loadLicenseView(@NonNull ViewHolder viewHolder) {
    if (expanded) {
      if (licenseText.isEmpty()) {
        viewHolder.binding.expandLicenseProgress.setVisibility(View.VISIBLE);
        AboutLibrariesModel model = getModel();
        if (model != null) {
          presenter.loadLicenseText(model,
              new AboutLibrariesItemPresenter.LicenseTextLoadCallback() {
                @Override public void onLicenseTextLoadComplete(@NonNull String text) {
                  setLicenseText(text);
                  expandLicense(viewHolder);
                }

                @Override public void onLicenseTextLoadError() {
                  setLicenseText("Error: Could not load license text");
                  collapseLicense(viewHolder);
                }
              });
        }
      } else {
        expandLicense(viewHolder);
      }
    } else {
      collapseLicense(viewHolder);
    }
  }

  @SuppressWarnings("WeakerAccess") void collapseLicense(@NonNull ViewHolder viewHolder) {
    viewHolder.binding.expandLicenseProgress.setVisibility(View.GONE);
    viewHolder.binding.expandLicenseText.setVisibility(View.GONE);
    viewHolder.binding.expandLicenseText.loadDataWithBaseURL(null, "", "text/plain", "UTF-8", null);
  }

  @SuppressWarnings("WeakerAccess") void expandLicense(@NonNull ViewHolder viewHolder) {
    viewHolder.binding.expandLicenseProgress.setVisibility(View.GONE);
    viewHolder.binding.expandLicenseText.setVisibility(View.VISIBLE);
    viewHolder.binding.expandLicenseText.loadDataWithBaseURL(null, licenseText, "text/plain",
        "UTF-8", null);
  }

  @Override public void unbindView(ViewHolder holder) {
    holder = Checker.checkNonNull(holder);
    super.unbindView(holder);
    holder.binding.expandLicenseHomepage.setOnClickListener(null);
    holder.binding.expandLicenseName.setText(null);
    presenter.stop();
  }

  @Override public ViewHolder getViewHolder(View view) {
    return new ViewHolder(view);
  }

  static class ViewHolder extends RecyclerView.ViewHolder {

    @NonNull final AdapterItemAboutBinding binding;

    ViewHolder(View view) {
      super(view);
      binding = AdapterItemAboutBinding.bind(view);
      binding.expandLicenseText.getSettings().setTextZoom(80);
      binding.expandLicenseProgress.setIndeterminate(true);
      binding.expandLicenseHomepage.setTextColor(Color.BLUE);
      binding.expandLicenseHomepage.setSingleLine(true);
    }
  }
}
