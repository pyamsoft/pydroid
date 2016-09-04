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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;
import com.pyamsoft.pydroid.R;
import java.util.List;

class AboutLicenseItem extends AbstractItem<AboutLicenseItem, AboutLicenseItem.ViewHolder>
    implements AboutLibrariesPresenter.View<AboutLicenseItem.ViewHolder> {

  @NonNull private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();
  @NonNull private final Licenses licenseName;
  @Nullable private AboutLibrariesPresenter<ViewHolder> presenter;

  AboutLicenseItem(@NonNull Licenses licenseName) {
    this.licenseName = licenseName;
  }

  @Override public int getType() {
    return R.id.fastadapter_about_license_item;
  }

  @Override public int getLayoutRes() {
    return R.layout.adapter_item_license;
  }

  @Override public ViewHolderFactory<? extends ViewHolder> getFactory() {
    return FACTORY;
  }

  @Override public void bindView(ViewHolder holder, List payloads) {
    super.bindView(holder, payloads);
    if (presenter == null) {
      presenter =
          new AboutLibrariesPresenterImpl<>(holder.itemView.getContext().getApplicationContext());
    }

    if (presenter.isBound()) {
      presenter.unbindView();
    }

    presenter.bindView(this);
    presenter.loadLicenseText(holder, licenseName);
  }

  @Override public void onLicenseTextLoaded(@NonNull ViewHolder holder, @NonNull String text) {
    holder.licenseText.setText(text);
    if (presenter != null) {
      presenter.unbindView();
    }
  }

  protected static class ItemFactory implements ViewHolderFactory<ViewHolder> {

    @Override public ViewHolder create(@NonNull View v) {
      return new ViewHolder(v);
    }
  }

  protected static class ViewHolder extends RecyclerView.ViewHolder {

    final TextView licenseText;

    public ViewHolder(View itemView) {
      super(itemView);
      licenseText = (TextView) itemView.findViewById(R.id.about_license_text);
    }
  }
}
