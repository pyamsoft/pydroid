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

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;
import com.pyamsoft.pydroid.R;
import com.pyamsoft.pydroid.R2;
import com.pyamsoft.pydroid.base.PersistLoader;
import com.pyamsoft.pydroid.util.NetworkUtil;
import com.pyamsoft.pydroid.util.PersistentCache;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.solovyev.android.checkout.ActivityCheckout;
import org.solovyev.android.checkout.BillingRequests;
import org.solovyev.android.checkout.Checkout;
import org.solovyev.android.checkout.Inventory;
import org.solovyev.android.checkout.Purchase;
import org.solovyev.android.checkout.RequestListener;
import org.solovyev.android.checkout.ResponseCodes;
import org.solovyev.android.checkout.Sku;

public class SupportDialog extends DialogFragment implements SocialMediaPresenter.View {

  @NonNull private static final String KEY_SUPPORT_PRESENTER = "key_support_presenter";
  @SuppressWarnings("WeakerAccess") SocialMediaPresenter presenter;
  @BindView(R2.id.support_about_app) Button aboutApp;
  @BindView(R2.id.google_play) ImageView googlePlay;
  @BindView(R2.id.google_plus) ImageView googlePlus;
  @BindView(R2.id.blogger) ImageView blogger;
  @BindView(R2.id.facebook) ImageView facebook;
  @BindView(R2.id.support_recycler) RecyclerView recyclerView;
  FastItemAdapter<SkuUIItem> fastItemAdapter;
  Inventory inAppPurchaseInventory;
  ActivityCheckout activityCheckout;
  private long loadedKey;
  private Unbinder unbinder;

  @Override public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof DonationActivity) {
      final DonationActivity donationActivity = (DonationActivity) context;
      activityCheckout = donationActivity.getCheckout();
    } else {
      throw new ClassCastException("Attached context is not instance of DonationActivity");
    }
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    inAppPurchaseInventory = activityCheckout.loadInventory();
    activityCheckout.createPurchaseFlow(new DonationPurchaseListener());

    loadedKey = PersistentCache.get()
        .load(KEY_SUPPORT_PRESENTER, savedInstanceState,
            new PersistLoader.Callback<SocialMediaPresenter>() {
              @NonNull @Override public PersistLoader<SocialMediaPresenter> createLoader() {
                return new SocialMediaPresenterLoader(getContext());
              }

              @Override public void onPersistentLoaded(@NonNull SocialMediaPresenter persist) {
                presenter = persist;
              }
            });
    setCancelable(true);
  }

  @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
    @SuppressLint("InflateParams") final View rootView =
        LayoutInflater.from(getActivity()).inflate(R.layout.dialog_support, null, false);
    unbinder = ButterKnife.bind(this, rootView);
    initDialog();
    return new AlertDialog.Builder(getActivity()).setNegativeButton("Later",
        (dialogInterface, i) -> {
          dialogInterface.dismiss();
        }).setView(rootView).create();
  }

  private void initDialog() {
    aboutApp.setOnClickListener(view1 -> presenter.clickAppPage(getActivity().getPackageName()));
    googlePlay.setOnClickListener(view1 -> presenter.clickGooglePlay());
    googlePlus.setOnClickListener(view1 -> presenter.clickGooglePlus());
    blogger.setOnClickListener(view1 -> presenter.clickBlogger());
    facebook.setOnClickListener(view1 -> presenter.clickFacebook());

    fastItemAdapter = new FastItemAdapter<>();
    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    recyclerView.setAdapter(fastItemAdapter);
    inAppPurchaseInventory.load().whenLoaded(new InventoryLoadedListener());

    fastItemAdapter.withOnClickListener((v, adapter, item, position) -> {
      attemptPurchase(item.sku());
      return true;
    });
  }

  @Override public void onSocialMediaClicked(@NonNull String link) {
    NetworkUtil.newLink(getContext().getApplicationContext(), link);
  }

  @Override public void onStart() {
    super.onStart();
    presenter.bindView(this);
  }

  @Override public void onStop() {
    super.onStop();
    presenter.unbindView();
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    PersistentCache.get().saveKey(outState, KEY_SUPPORT_PRESENTER, loadedKey);
    super.onSaveInstanceState(outState);
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }

  @Override public void onDestroy() {
    super.onDestroy();
    if (!getActivity().isChangingConfigurations()) {
      PersistentCache.get().unload(loadedKey);
    }
  }

  void attemptPurchase(@NonNull SkuItem sku) {
    if (sku.isPurchased()) {
      final String token = sku.token();
      if (token == null) {
        throw new IllegalStateException(
            "Sku " + sku.sku().title + " has been purchased but token is NULL");
      }

      consumeInAppPurchaseItem(token, new ConsumeListener());
    } else {
      checkoutInAppPurchaseItem(sku.sku());
    }
  }

  private void checkoutInAppPurchaseItem(@NonNull Sku sku) {
    activityCheckout.whenReady(new Checkout.ListenerAdapter() {
      @Override public void onReady(@NonNull BillingRequests requests) {
        requests.purchase(sku, null, activityCheckout.getPurchaseFlow());
      }
    });
  }

  private void consumeInAppPurchaseItem(@NonNull String token,
      @NonNull RequestListener<Object> consumeListener) {
    activityCheckout.whenReady(new Checkout.ListenerAdapter() {
      @Override public void onReady(@NonNull BillingRequests requests) {
        requests.consume(token, consumeListener);
      }
    });
  }

  class InventoryLoadedListener implements Inventory.Listener {

    @Override public void onLoaded(@NonNull Inventory.Products products) {
      final Inventory.Product product = products.get(DonationActivity.IN_APP_PRODUCT_ID);
      // TODO show loading
      fastItemAdapter.clear();
      if (product.supported) {
        final List<SkuItem> skuItemList = new ArrayList<>();
        for (Sku sku : product.getSkus()) {
          final Purchase purchase = product.getPurchaseInState(sku, Purchase.State.PURCHASED);
          skuItemList.add(SkuItem.create(sku, purchase == null ? null : purchase.token));
        }

        Collections.sort(skuItemList, (o1, o2) -> {
          final long o1Price = o1.sku().detailedPrice.amount;
          final long o2Price = o2.sku().detailedPrice.amount;
          if (o1Price > o2Price) {
            return 1;
          } else if (o1Price < o2Price) {
            return -1;
          } else {
            return 0;
          }
        });

        for (final SkuItem skuItem : skuItemList) {
          fastItemAdapter.add(new SkuUIItem(skuItem));
        }

        fastItemAdapter.notifyDataSetChanged();
        // TODO finish loading
      } else {
        // TODO finish loading
        // TODO show an empty view with error message
      }
    }
  }

  abstract class BaseRequestListener<T> implements RequestListener<T> {

    void processResult() {
      inAppPurchaseInventory.load().whenLoaded(new InventoryLoadedListener());
    }
  }

  class DonationPurchaseListener extends BaseRequestListener<Purchase> {

    @Override void processResult() {
      super.processResult();
      // TODO any additional stuff
    }

    @Override public void onSuccess(@NonNull Purchase result) {
      processResult();
    }

    @Override public void onError(int response, @NonNull Exception e) {
      if (response == ResponseCodes.ITEM_ALREADY_OWNED) {
        processResult();
      } else {
        // TODO show error or something
      }
    }
  }

  class ConsumeListener extends BaseRequestListener<Object> {

    @Override void processResult() {
      super.processResult();
      // TODO any additional stuff
    }

    @Override public void onSuccess(@NonNull Object result) {
      processResult();
    }

    @Override public void onError(int response, @NonNull Exception e) {
      // it is possible that our data is not synchronized with data on Google Play => need to handle some errors
      if (response == ResponseCodes.ITEM_NOT_OWNED) {
        processResult();
      } else {
        // TODO error when consuming
      }
    }
  }
}
