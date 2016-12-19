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

package com.pyamsoft.pydroid.donate;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.pyamsoft.pydroid.R;
import com.pyamsoft.pydroid.databinding.DialogDonateBinding;
import com.pyamsoft.pydroid.util.AppUtil;
import org.solovyev.android.checkout.Inventory;
import org.solovyev.android.checkout.ProductTypes;
import org.solovyev.android.checkout.Purchase;
import org.solovyev.android.checkout.Sku;
import timber.log.Timber;

public class DonateDialog extends DialogFragment implements DonatePresenter.View {

  @NonNull public static final String TAG = "SupportDialog";
  private FastItemAdapter<SkuUIItem> fastItemAdapter;
  private DialogDonateBinding binding;

  public static void show(@NonNull FragmentManager fragmentManager) {
    final Bundle args = new Bundle();
    final DonateDialog fragment = new DonateDialog();
    fragment.setArguments(args);
    AppUtil.guaranteeSingleDialogFragment(fragmentManager, fragment, TAG);
  }

  @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
    final Dialog dialog = super.onCreateDialog(savedInstanceState);
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    return dialog;
  }

  @Override public void onBillingSuccess() {
    Toast.makeText(getContext(), R.string.purchase_success_msg, Toast.LENGTH_SHORT).show();
  }

  @Override public void onBillingError() {
    Toast.makeText(getContext(), R.string.purchase_error_msg, Toast.LENGTH_SHORT).show();
  }

  @Override public void onProcessResultSuccess() {
    Timber.i("Process Billing result was a success!");
  }

  @Override public void onProcessResultError() {
    Timber.e("Process result error");
    onBillingError();
  }

  @Override public void onProcessResultFailed() {
    Timber.e("Process result failed");
    onBillingError();
  }

  @SuppressWarnings("WeakerAccess") @CheckResult @NonNull DonatePresenter getDonatePresenter() {
    final Activity activity = getActivity();
    if (activity instanceof DonationActivity) {
      return ((DonationActivity) activity).getDonatePresenter();
    } else {
      throw new IllegalStateException("SupportDialog activity is not DonationActivity");
    }
  }

  @Override public void onInventoryLoaded(@NonNull Inventory.Products products) {
    binding.supportLoading.setVisibility(View.VISIBLE);
    binding.supportRecycler.setVisibility(View.GONE);

    fastItemAdapter.clear();
    final Inventory.Product product = products.get(ProductTypes.IN_APP);
    if (product.supported) {
      // Only reveal non-consumable items
      for (Sku sku : product.getSkus()) {
        final Purchase purchase = product.getPurchaseInState(sku, Purchase.State.PURCHASED);
        final String token;
        if (purchase == null) {
          token = null;
        } else {
          token = purchase.token;
        }
        fastItemAdapter.add(new SkuUIItem(sku, token));
      }

      if (fastItemAdapter.getAdapterItems().isEmpty()) {
        loadEmptyIAPView();
      } else {
        loadIAPView();
      }
    } else {
      loadUnsupportedIAPView();
    }
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setCancelable(true);
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    fastItemAdapter = new FastItemAdapter<>();
    binding = DialogDonateBinding.inflate(inflater, container, false);
    return binding.getRoot();
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    initializeDialog();
    initializeDonations();
  }

  private void initializeDialog() {
    binding.supportToolbar.setTitle("Support pyamsoft");
    binding.supportToolbar.setNavigationOnClickListener(view -> dismiss());
    ViewCompat.setElevation(binding.supportToolbar, AppUtil.convertToDP(getContext(), 4));
  }

  private void initializeDonations() {
    binding.supportLoadingProgress.setIndeterminate(true);
    binding.supportIapEmpty.setVisibility(View.GONE);
    binding.supportRecycler.setVisibility(View.GONE);
    binding.supportLoading.setVisibility(View.VISIBLE);

    fastItemAdapter.withSelectable(true);
    fastItemAdapter.withOnClickListener((v, adapter, item, position) -> {
      getDonatePresenter().checkoutInAppPurchaseItem(item.getModel());
      return true;
    });

    fastItemAdapter.getItemAdapter().withComparator((o1, o2) -> {
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

    binding.supportRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
    binding.supportRecycler.setAdapter(fastItemAdapter);
  }

  @Override public void onResume() {
    super.onResume();
    // The dialog is super small for some reason. We have to set the size manually, in onResume
    final Window window = getDialog().getWindow();
    if (window != null) {
      window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
          WindowManager.LayoutParams.WRAP_CONTENT);
    }

    getDonatePresenter().loadInventory();
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    binding.unbind();
  }

  private void loadUnsupportedIAPView() {
    Timber.e("Load UNSUPPORTED");
    binding.supportLoading.setVisibility(View.GONE);
    binding.supportRecycler.setVisibility(View.GONE);
    binding.supportIapEmpty.setVisibility(View.VISIBLE);
    binding.supportIapEmptyText.setText(getString(R.string.iap_unsupported));
  }

  private void loadEmptyIAPView() {
    Timber.w("Load EMPTY");
    binding.supportLoading.setVisibility(View.GONE);
    binding.supportRecycler.setVisibility(View.GONE);
    binding.supportIapEmpty.setVisibility(View.VISIBLE);
    binding.supportIapEmptyText.setText(getString(R.string.iap_empty));
  }

  private void loadIAPView() {
    Timber.i("Load IAP");
    binding.supportLoading.setVisibility(View.GONE);
    binding.supportIapEmpty.setVisibility(View.GONE);
    binding.supportIapEmptyText.setText(null);
    binding.supportRecycler.setVisibility(View.VISIBLE);
    fastItemAdapter.notifyDataSetChanged();
  }
}
