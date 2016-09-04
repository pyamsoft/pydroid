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
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IExpandable;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;
import com.pyamsoft.pydroid.R;
import com.pyamsoft.pydroid.util.NetworkUtil;
import java.util.ArrayList;
import java.util.List;

class ExpandableAboutItem extends AbstractItem<ExpandableAboutItem, ExpandableAboutItem.ViewHolder>
    implements IExpandable<ExpandableAboutItem, IItem> {

  @NonNull private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();
  @NonNull final String licenseHomepage;
  @NonNull private final String licenseName;
  private List<IItem> mSubItems;
  private boolean expanded = false;

  @NonNull private final FastAdapter.OnClickListener<ExpandableAboutItem> onClickListener =
      (v, adapter, item, position) -> {
        if (!item.isExpanded()) {
          ViewCompat.animate(v.findViewById(R.id.expand_license_icon)).rotation(180).start();
        } else {
          ViewCompat.animate(v.findViewById(R.id.expand_license_icon)).rotation(0).start();
        }
        return true;
      };

  ExpandableAboutItem(@NonNull String licenseName, @NonNull String licenseHomepage) {
    this.licenseName = licenseName;
    this.licenseHomepage = licenseHomepage;
  }

  @CheckResult @Override public boolean isExpanded() {
    return expanded;
  }

  @NonNull @CheckResult @Override public ExpandableAboutItem withIsExpanded(boolean expanded) {
    this.expanded = expanded;
    return this;
  }

  @NonNull @CheckResult @Override public List<IItem> getSubItems() {
    return mSubItems;
  }

  @CheckResult @Override public boolean isAutoExpanding() {
    return true;
  }

  @NonNull @CheckResult @Override
  public ExpandableAboutItem withSubItems(@NonNull List<IItem> subItems) {
    this.mSubItems = subItems;
    return this;
  }

  @NonNull @CheckResult public ExpandableAboutItem addLicense(@NonNull IItem item) {
    final List<IItem> items = new ArrayList<>();
    items.add(item);
    return withSubItems(items);
  }

  @Override public FastAdapter.OnClickListener<ExpandableAboutItem> getOnItemClickListener() {
    return onClickListener;
  }

  @CheckResult @Override public boolean isSelectable() {
    return false;
  }

  @Override public int getType() {
    return R.id.fastadapter_expandable_about_item;
  }

  @Override public int getLayoutRes() {
    return R.layout.adapter_item_about_expand;
  }

  @Override public void bindView(@NonNull ViewHolder viewHolder, List payloads) {
    super.bindView(viewHolder, payloads);
    viewHolder.licenseHomepage.setOnClickListener(null);

    //make sure all animations are stopped
    viewHolder.arrowIcon.clearAnimation();
    if (isExpanded()) {
      ViewCompat.setRotation(viewHolder.arrowIcon, 0);
    } else {
      ViewCompat.setRotation(viewHolder.arrowIcon, 180);
    }

    viewHolder.licenseName.setText(licenseName);
    viewHolder.licenseHomepage.setText("HomePage");
    viewHolder.licenseHomepage.setTextColor(Color.BLUE);
    viewHolder.licenseHomepage.setSingleLine(true);
    viewHolder.licenseHomepage.setOnClickListener(
        view -> NetworkUtil.newLink(view.getContext().getApplicationContext(), licenseHomepage));
  }

  @Override public ViewHolderFactory<? extends ViewHolder> getFactory() {
    return FACTORY;
  }

  protected static class ItemFactory implements ViewHolderFactory<ViewHolder> {

    @Override public ViewHolder create(@NonNull View v) {
      return new ViewHolder(v);
    }
  }

  protected static class ViewHolder extends RecyclerView.ViewHolder {

    final TextView licenseName;
    final TextView licenseHomepage;
    final ImageView arrowIcon;

    public ViewHolder(View view) {
      super(view);
      licenseName = (TextView) view.findViewById(R.id.expand_license_name);
      licenseHomepage = (TextView) view.findViewById(R.id.expand_license_homepage);
      arrowIcon = (ImageView) view.findViewById(R.id.expand_license_icon);
    }
  }
}
