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
import com.pyamsoft.pydroid.about.AboutLibrariesItemPresenter;
import com.pyamsoft.pydroid.about.AboutLibrariesModel;
import com.pyamsoft.pydroid.helper.Checker;
import com.pyamsoft.pydroid.loader.ImageLoader;
import com.pyamsoft.pydroid.loader.LoaderHelper;
import com.pyamsoft.pydroid.loader.loaded.Loaded;
import com.pyamsoft.pydroid.ui.PYDroidInjector;
import com.pyamsoft.pydroid.ui.R;
import com.pyamsoft.pydroid.ui.databinding.AdapterItemAboutBinding;
import com.pyamsoft.pydroid.util.NetworkUtil;
import java.util.List;

class AboutLibrariesItem extends
    GenericAbstractItem<AboutLibrariesModel, AboutLibrariesItem, AboutLibrariesItem.ViewHolder> {

  AboutLibrariesItemPresenter presenter;
  @SuppressWarnings("WeakerAccess") @NonNull Loaded arrowLoad = LoaderHelper.empty();
  @SuppressWarnings("WeakerAccess") @Nullable ViewPropertyAnimatorCompat arrowAnimation;
  @SuppressWarnings("WeakerAccess") boolean expanded;
  @NonNull private String licenseText;

  AboutLibrariesItem(@NonNull AboutLibrariesModel item) {
    super(Checker.checkNonNull(item));
    licenseText = "";
    PYDroidInjector.get().provideComponent().plusAboutLibrariesComponent().inject(this);
  }

  @SuppressWarnings("WeakerAccess") void setLicenseText(@NonNull String licenseText) {
    this.licenseText = Checker.checkNonNull(licenseText);
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

  @Override public void bindView(@NonNull final ViewHolder viewHolder, List<Object> payloads) {
    super.bindView(viewHolder, payloads);

    arrowLoad = LoaderHelper.unload(arrowLoad);
    arrowLoad =
        ImageLoader.fromResource(viewHolder.itemView.getContext(), R.drawable.ic_arrow_up_24dp)
            .into(viewHolder.binding.expandLicenseIcon);

    cancelArrowAnimation();
    if (expanded) {
      ViewCompat.setRotation(viewHolder.binding.expandLicenseIcon, 0);
      expandLicense(viewHolder);
    } else {
      ViewCompat.setRotation(viewHolder.binding.expandLicenseIcon, 180);
      collapseLicense(viewHolder);
    }

    viewHolder.binding.expandLicenseName.setText(getModel().name());
    viewHolder.binding.expandLicenseHomepage.setOnClickListener(
        view -> NetworkUtil.newLink(view.getContext().getApplicationContext(),
            getModel().homepage()));

    viewHolder.itemView.setOnClickListener(v -> {
      expanded = !expanded;
      cancelArrowAnimation();
      arrowAnimation =
          ViewCompat.animate(viewHolder.binding.expandLicenseIcon).rotation(expanded ? 0 : 180);
      loadLicenseView(viewHolder);
    });

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
    holder.binding.expandLicenseIcon.setImageDrawable(null);
    holder.itemView.setOnClickListener(null);
    presenter.stop();
    presenter.destroy();
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
