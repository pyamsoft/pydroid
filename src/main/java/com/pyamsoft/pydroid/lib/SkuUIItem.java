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

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;
import com.pyamsoft.pydroid.R;
import com.pyamsoft.pydroid.R2;
import java.util.List;

class SkuUIItem extends AbstractItem<SkuUIItem, SkuUIItem.ViewHolder> {

  @NonNull private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();

  @NonNull private final SkuItem skuItem;

  SkuUIItem(@NonNull SkuItem skuItem) {
    this.skuItem = skuItem;
  }

  @CheckResult @NonNull final SkuItem sku() {
    return skuItem;
  }

  @Override public int getType() {
    return 0;
  }

  @Override public int getLayoutRes() {
    return R.layout.adapter_item_iap;
  }

  @Override public ViewHolderFactory<? extends ViewHolder> getFactory() {
    return FACTORY;
  }

  @Override public void bindView(ViewHolder holder, List payloads) {
    super.bindView(holder, payloads);
    holder.title.setText(skuItem.sku().title);
    holder.description.setText(skuItem.sku().description);
    holder.price.setText(skuItem.sku().price);
  }

  protected static class ViewHolder extends RecyclerView.ViewHolder {

    @NonNull final Unbinder unbinder;
    @BindView(R2.id.purchase_iap_title) TextView title;
    @BindView(R2.id.purchase_iap_description) TextView description;
    @BindView(R2.id.purchase_iap_price) TextView price;

    public ViewHolder(View itemView) {
      super(itemView);
      unbinder = ButterKnife.bind(this, itemView);
    }
  }

  @SuppressWarnings("WeakerAccess") protected static class ItemFactory
      implements ViewHolderFactory<ViewHolder> {
    @Override public ViewHolder create(View v) {
      return new ViewHolder(v);
    }
  }
}
