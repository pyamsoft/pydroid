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

package com.pyamsoft.pydroid.support;

import android.os.Build;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import com.mikepenz.fastadapter.items.GenericAbstractItem;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;
import com.pyamsoft.pydroid.R;
import com.pyamsoft.pydroid.databinding.AdapterItemIapBinding;
import java.util.List;
import org.solovyev.android.checkout.Sku;

class SkuUIItem extends GenericAbstractItem<SkuModel, SkuUIItem, SkuUIItem.ViewHolder> {

  @NonNull private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();

  SkuUIItem(@NonNull Sku sku, @Nullable String token) {
    super(SkuModel.create(sku, token));
  }

  @NonNull @CheckResult Sku getSku() {
    return getModel().sku();
  }

  @CheckResult boolean isPurchased() {
    return getModel().token() != null;
  }

  @CheckResult @NonNull String getToken() {
    final String token = getModel().token();
    if (token == null) {
      throw new NullPointerException("Token is NULL");
    }

    return token;
  }

  @Override public int getType() {
    return R.id.fastadapter_iap;
  }

  @Override public int getLayoutRes() {
    return R.layout.adapter_item_iap;
  }

  @Override public ViewHolderFactory<? extends ViewHolder> getFactory() {
    return FACTORY;
  }

  @Override public void bindView(ViewHolder holder, List<Object> payloads) {
    super.bindView(holder, payloads);
    holder.bind(getModel().sku());
  }

  @Override public void unbindView(ViewHolder holder) {
    super.unbindView(holder);
    holder.unbind();
  }

  protected static class ViewHolder extends RecyclerView.ViewHolder {

    @NonNull private final AdapterItemIapBinding binding;

    public ViewHolder(View itemView) {
      super(itemView);
      binding = AdapterItemIapBinding.bind(itemView);
    }

    @SuppressWarnings("deprecation") @CheckResult @NonNull
    private static Spanned fromHtml(@NonNull String description) {
      final Spanned html;
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        html = Html.fromHtml(description, Html.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE);
      } else {
        html = Html.fromHtml(description);
      }
      return html;
    }

    /**
     * Titles will sometimes contain the app name in (), strip them
     */
    @NonNull @CheckResult private static String formatTitle(@NonNull String title) {
      final String formatted;
      final int i = title.indexOf("(");
      if (i > 0) {
        if (title.charAt(i - 1) == ' ') {
          formatted = title.substring(0, i - 1);
        } else {
          formatted = title.substring(0, i);
        }
      } else {
        formatted = title;
      }

      return formatted;
    }

    void bind(@NonNull Sku sku) {
      binding.purchaseIapTitle.setText(formatTitle(sku.title));
      binding.purchaseIapDescription.setText(fromHtml(sku.description));
      binding.purchaseIapPrice.setText(sku.price);
    }

    void unbind() {
      binding.purchaseIapTitle.setText(null);
      binding.purchaseIapDescription.setText(null);
      binding.purchaseIapPrice.setText(null);
    }
  }

  @SuppressWarnings("WeakerAccess") protected static class ItemFactory
      implements ViewHolderFactory<ViewHolder> {
    @Override public ViewHolder create(View v) {
      return new ViewHolder(v);
    }
  }
}
