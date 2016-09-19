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
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.CheckResult;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;
import com.pyamsoft.pydroid.BuildConfig;
import com.pyamsoft.pydroid.R;
import com.pyamsoft.pydroid.R2;
import com.pyamsoft.pydroid.app.widget.VectorTextView;
import com.pyamsoft.pydroid.base.PersistLoader;
import com.pyamsoft.pydroid.util.AppUtil;
import com.pyamsoft.pydroid.util.NetworkUtil;
import com.pyamsoft.pydroid.util.PersistentCache;
import com.pyamsoft.pyprolib.Pro;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.solovyev.android.checkout.ActivityCheckout;
import org.solovyev.android.checkout.Billing;
import org.solovyev.android.checkout.BillingRequests;
import org.solovyev.android.checkout.Checkout;
import org.solovyev.android.checkout.Inventory;
import org.solovyev.android.checkout.ProductTypes;
import org.solovyev.android.checkout.Products;
import org.solovyev.android.checkout.Purchase;
import org.solovyev.android.checkout.PurchaseVerifier;
import org.solovyev.android.checkout.RequestListener;
import org.solovyev.android.checkout.ResponseCodes;
import org.solovyev.android.checkout.Sku;
import timber.log.Timber;

public class SupportDialog extends DialogFragment
    implements SocialMediaPresenter.View, SupportPresenter.View {

  @NonNull private static final String KEY_SOCIAL_PRESENTER = "key_social_presenter";
  @NonNull private static final String KEY_SUPPORT_PRESENTER = "key_support_presenter";
  @NonNull private static final String KEY_APP_ICON = "key_app_icon";
  @SuppressWarnings("WeakerAccess") SocialMediaPresenter socialMediaPresenter;
  @SuppressWarnings("WeakerAccess") SupportPresenter supportPresenter;
  @BindView(R2.id.support_about_app) Button aboutApp;
  @BindView(R2.id.google_play) ImageView googlePlay;
  @BindView(R2.id.google_plus) ImageView googlePlus;
  @BindView(R2.id.blogger) ImageView blogger;
  @BindView(R2.id.facebook) ImageView facebook;
  @BindView(R2.id.support_recycler) RecyclerView recyclerView;
  @BindView(R2.id.support_iap_empty) FrameLayout emptyIAP;
  @BindView(R2.id.support_loading) FrameLayout loadingLayout;
  @BindView(R2.id.support_loading_progress) ProgressBar loadingProgress;
  @BindView(R2.id.support_iap_empty_text) TextView emptyIAPText;
  @BindView(R2.id.support_upgrade) LinearLayout upgradeApplication;
  @BindView(R2.id.purchase_iap_title) VectorTextView upgradeTitle;
  @BindView(R2.id.purchase_iap_description) TextView upgradeDescription;
  FastItemAdapter<SkuUIItem> fastItemAdapter;
  Inventory inAppPurchaseInventory;
  @SuppressWarnings("WeakerAccess") ActivityCheckout checkout;
  private long socialMediaKey;
  private long supportKey;
  private Unbinder unbinder;
  @DrawableRes private int appIcon;

  static void show(@NonNull FragmentManager fragmentManager, @DrawableRes int appIcon) {
    final Bundle args = new Bundle();
    final SupportDialog fragment = new SupportDialog();
    args.putInt(KEY_APP_ICON, appIcon);
    fragment.setArguments(args);
    AppUtil.guaranteeSingleDialogFragment(fragmentManager, fragment, "SupportDialog");
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setCancelable(true);
    checkout = CheckoutFactory.create(getActivity());
    checkout.start();
    checkout.createPurchaseFlow(new DonationPurchaseListener());
    inAppPurchaseInventory = checkout.loadInventory();

    appIcon = getArguments().getInt(KEY_APP_ICON, 0);

    socialMediaKey = PersistentCache.get()
        .load(KEY_SOCIAL_PRESENTER, savedInstanceState,
            new PersistLoader.Callback<SocialMediaPresenter>() {
              @NonNull @Override public PersistLoader<SocialMediaPresenter> createLoader() {
                return new SocialMediaPresenterLoader(getContext());
              }

              @Override public void onPersistentLoaded(@NonNull SocialMediaPresenter persist) {
                socialMediaPresenter = persist;
              }
            });

    supportKey = PersistentCache.get()
        .load(KEY_SUPPORT_PRESENTER, savedInstanceState,
            new PersistLoader.Callback<SupportPresenter>() {
              @NonNull @Override public PersistLoader<SupportPresenter> createLoader() {
                return new SupportPresenterLoader(getContext());
              }

              @Override public void onPersistentLoaded(@NonNull SupportPresenter persist) {
                supportPresenter = persist;
              }
            });
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
    if (appIcon != 0) {
      upgradeTitle.setCompoundDrawableResWithIntrinsicBounds(appIcon, 0, 0, 0);
    }
    upgradeTitle.setText(R.string.upgrade_to_pro_title);
    upgradeDescription.setText(R.string.upgrade_to_pro_desc);

    upgradeApplication.setOnClickListener(
        v -> socialMediaPresenter.clickAppPage(getActivity().getPackageName() + "pro"));
    aboutApp.setOnClickListener(
        view1 -> socialMediaPresenter.clickAppPage(getActivity().getPackageName()));
    googlePlay.setOnClickListener(view1 -> socialMediaPresenter.clickGooglePlay());
    googlePlus.setOnClickListener(view1 -> socialMediaPresenter.clickGooglePlus());
    blogger.setOnClickListener(view1 -> socialMediaPresenter.clickBlogger());
    facebook.setOnClickListener(view1 -> socialMediaPresenter.clickFacebook());

    loadingProgress.setIndeterminate(true);
    emptyIAP.setVisibility(View.GONE);
    recyclerView.setVisibility(View.GONE);
    loadingLayout.setVisibility(View.VISIBLE);

    fastItemAdapter = new FastItemAdapter<>();
    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    recyclerView.setAdapter(fastItemAdapter);
    fastItemAdapter.withOnClickListener((v, adapter, item, position) -> {
      checkoutInAppPurchaseItem(item.getSku());
      return true;
    });

    inAppPurchaseInventory.load().whenLoaded(new InventoryLoadedListener());
  }

  @Override public void onSocialMediaClicked(@NonNull String link) {
    NetworkUtil.newLink(getContext().getApplicationContext(), link);
  }

  @Override public void onStart() {
    super.onStart();
    socialMediaPresenter.bindView(this);
    supportPresenter.bindView(this);
  }

  @Override public void onStop() {
    super.onStop();
    socialMediaPresenter.unbindView();
    supportPresenter.unbindView();
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    PersistentCache.get().saveKey(outState, KEY_SUPPORT_PRESENTER, supportKey);
    PersistentCache.get().saveKey(outState, KEY_SOCIAL_PRESENTER, socialMediaKey);
    super.onSaveInstanceState(outState);
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }

  @Override public void onDestroy() {
    super.onDestroy();
    checkout.stop();
    if (!getActivity().isChangingConfigurations()) {
      PersistentCache.get().unload(socialMediaKey);
      PersistentCache.get().unload(supportKey);
    }
  }

  void checkoutInAppPurchaseItem(@NonNull Sku sku) {
    checkout.whenReady(new Checkout.ListenerAdapter() {
      @Override public void onReady(@NonNull BillingRequests requests) {
        requests.purchase(sku, null, checkout.getPurchaseFlow());
      }
    });
  }

  @Override public void onResume() {
    super.onResume();
    if (Pro.isPro(getContext())) {
      upgradeApplication.setVisibility(View.GONE);
    }
  }

  void loadUnsupportedIAPView() {
    Timber.e("Load UNSUPPORTED");
    loadingLayout.setVisibility(View.GONE);
    recyclerView.setVisibility(View.GONE);
    emptyIAP.setVisibility(View.VISIBLE);
    emptyIAPText.setText(getString(R.string.iap_unsupported));
  }

  void loadEmptyIAPView() {
    Timber.w("Load EMPTY");
    loadingLayout.setVisibility(View.GONE);
    recyclerView.setVisibility(View.GONE);
    emptyIAP.setVisibility(View.VISIBLE);
    emptyIAPText.setText(getString(R.string.iap_empty));
  }

  void loadIAPView() {
    Timber.i("Load IAP");
    loadingLayout.setVisibility(View.GONE);
    emptyIAP.setVisibility(View.GONE);
    emptyIAPText.setText(null);
    recyclerView.setVisibility(View.VISIBLE);
    fastItemAdapter.notifyDataSetChanged();
  }

  @Override public void onDonationResult(int requestCode, int resultCode, @Nullable Intent data) {
    checkout.onActivityResult(requestCode, resultCode, data);
  }

  static final class CheckoutFactory {
    @NonNull private static final String SKU_DONATE = ".donate";
    @NonNull private static final String SKU_DONATE_ONE = SKU_DONATE + ".one";
    @NonNull private static final String SKU_DONATE_TWO = SKU_DONATE + ".two";
    @NonNull private static final String SKU_DONATE_FIVE = SKU_DONATE + ".five";
    @NonNull private static final String SKU_DONATE_TEN = SKU_DONATE + ".ten";

    private CheckoutFactory() {
      throw new RuntimeException("No instances");
    }

    @CheckResult @NonNull static ActivityCheckout create(@NonNull Activity activity) {
      final Context appContext = activity.getApplicationContext();
      final String packageName = appContext.getPackageName();
      final String appSpecificSkuDonateOne = packageName + SKU_DONATE_ONE;
      final String appSpecificSkuDonateTwo = packageName + SKU_DONATE_TWO;
      final String appSpecificSkuDonateFive = packageName + SKU_DONATE_FIVE;
      final String appSpecificSkuDonateTen = packageName + SKU_DONATE_TEN;

      final List<String> skuList = new ArrayList<>();
      skuList.add(appSpecificSkuDonateOne);
      skuList.add(appSpecificSkuDonateTwo);
      skuList.add(appSpecificSkuDonateFive);
      skuList.add(appSpecificSkuDonateTen);

      if (BuildConfig.DEBUG) {
        skuList.add("android.test.purchased");
        skuList.add("android.test.canceled");
        skuList.add("android.test.refunded");
        skuList.add("android.test.item_unavailable");
      }

      return Checkout.forActivity(activity,
          new Billing(appContext, new DonationBillingConfiguration(packageName)),
          Products.create().add(ProductTypes.IN_APP, skuList));
    }

    static class DonationBillingConfiguration extends Billing.DefaultConfiguration {

      @NonNull private final String publicKey;

      DonationBillingConfiguration(@NonNull String publicKey) {
        this.publicKey = publicKey;
      }

      @NonNull @Override public String getPublicKey() {
        return publicKey;
      }

      /**
       * We do not really need any purchase verification as they are all just donations anyway.
       * Our public key is not used, so the default verifier fails anyway.
       *
       * Pass a verifier which always passes
       */
      @NonNull @Override public PurchaseVerifier getPurchaseVerifier() {
        return new AlwaysPurchaseVerifier();
      }

      static class AlwaysPurchaseVerifier implements PurchaseVerifier {

        /**
         * Verify all purchases as 'valid'
         */
        @Override public void verify(@NonNull List<Purchase> purchases,
            @NonNull RequestListener<List<Purchase>> listener) {
          final List<Purchase> verifiedPurchases = new ArrayList<>(purchases.size());
          for (Purchase purchase : purchases) {
            verifiedPurchases.add(purchase);
          }
          listener.onSuccess(verifiedPurchases);
        }
      }
    }
  }

  class InventoryLoadedListener implements Inventory.Listener {

    @Override public void onLoaded(@NonNull Inventory.Products products) {
      loadingLayout.setVisibility(View.VISIBLE);
      recyclerView.setVisibility(View.GONE);

      fastItemAdapter.clear();
      final Inventory.Product product = products.get(ProductTypes.IN_APP);
      if (product.supported) {
        Timber.i("IAP Billing is supported");
        final List<SkuUIItem> skuList = new ArrayList<>();
        // Only reveal non-consumable items
        for (Sku sku : product.getSkus()) {
          Timber.d("Add sku: %s", sku.id);
          skuList.add(new SkuUIItem(sku));
        }

        Collections.sort(skuList, (o1, o2) -> {
          final long o1Price = o1.getSku().detailedPrice.amount;
          final long o2Price = o2.getSku().detailedPrice.amount;
          if (o1Price > o2Price) {
            return 1;
          } else if (o1Price < o2Price) {
            return -1;
          } else {
            return 0;
          }
        });

        fastItemAdapter.add(skuList);
        if (fastItemAdapter.getAdapterItems().isEmpty()) {
          loadEmptyIAPView();
        } else {
          loadIAPView();
        }
      } else {
        loadUnsupportedIAPView();
      }
    }
  }

  abstract class BaseRequestListener<T> implements RequestListener<T> {

    void processResult() {
      inAppPurchaseInventory.load().whenLoaded(new InventoryLoadedListener());
    }

    @CallSuper @Override public void onError(int response, @NonNull Exception e) {
      Timber.e(e, "Billing Error");
      processResult();
    }
  }

  class DonationPurchaseListener extends BaseRequestListener<Purchase> {

    @Override public void onSuccess(@NonNull Purchase result) {
      Timber.d("Consume the consumable purchase");
      checkout.whenReady(new Checkout.ListenerAdapter() {
        @Override public void onReady(@NonNull BillingRequests requests) {
          requests.consume(result.token, new ConsumeListener());
        }
      });
    }

    @Override public void onError(int response, @NonNull Exception e) {
      if (response != ResponseCodes.USER_CANCELED) {
        Toast.makeText(getContext(),
            "An error occurred during purchase attempt, please try again later", Toast.LENGTH_SHORT)
            .show();
      } else {
        super.onError(response, e);
      }
    }
  }

  class ConsumeListener extends BaseRequestListener<Object> {

    @Override public void onSuccess(@NonNull Object result) {
      processResult();
      Toast.makeText(getContext(), "Thank you for your purchase!", Toast.LENGTH_SHORT).show();
    }

    @Override public void onError(int response, @NonNull Exception e) {
      super.onError(response, e);
      // it is possible that our data is not synchronized with data on Google Play => need to handle some errors
      if (response == ResponseCodes.ITEM_NOT_OWNED) {
        Timber.w("CONSUME");
      } else {
        Toast.makeText(getContext(),
            "An error occurred during purchase attempt, please try again later", Toast.LENGTH_SHORT)
            .show();
      }
    }
  }
}
