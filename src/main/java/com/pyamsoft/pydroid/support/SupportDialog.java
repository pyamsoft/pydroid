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

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.pyamsoft.pydroid.R;
import com.pyamsoft.pydroid.base.ActivityBase;
import com.pyamsoft.pydroid.social.SocialMediaPresenter;
import com.pyamsoft.pydroid.social.SocialMediaPresenterImpl;
import com.pyamsoft.pydroid.social.SocialMediaView;
import com.pyamsoft.pydroid.util.NetworkUtil;
import com.pyamsoft.pydroid.util.StringUtil;
import timber.log.Timber;

public class SupportDialog extends DialogFragment implements View.OnClickListener, SocialMediaView {

  @NonNull private static final String SKU_DONATE_ONE = ".donate.one";
  @NonNull private static final String SKU_DONATE_TWO = ".donate.two";
  @NonNull private static final String SKU_DONATE_FIVE = ".donate.five";
  @NonNull private static final String SKU_DONATE_TEN = ".donate.ten";
  @NonNull private static final String ARG_PACKAGE = "package";
  private final SocialMediaPresenter presenter;
  private String APP_SKU_DONATE_ONE;
  private String APP_SKU_DONATE_TWO;
  private String APP_SKU_DONATE_FIVE;
  private String APP_SKU_DONATE_TEN;
  private String packageName;

  public SupportDialog() {
    super();
    presenter = new SocialMediaPresenterImpl();
  }

  public static SupportDialog newInstance(final @NonNull String packageName) {
    final SupportDialog fragment = new SupportDialog();
    final Bundle args = new Bundle();
    args.putSerializable(ARG_PACKAGE, packageName);
    fragment.setArguments(args);
    return fragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Timber.d("onCreate");
    presenter.create();

    packageName = getArguments().getString(ARG_PACKAGE);
    if (packageName == null) {
      throw new NullPointerException("Package Name cannot be NULL");
    }

    APP_SKU_DONATE_ONE = packageName + SKU_DONATE_ONE;
    APP_SKU_DONATE_TWO = packageName + SKU_DONATE_TWO;
    APP_SKU_DONATE_FIVE = packageName + SKU_DONATE_FIVE;
    APP_SKU_DONATE_TEN = packageName + SKU_DONATE_TEN;

    setCancelable(true);
  }

  @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
    return init();
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    Timber.d("onDestroyView");
    presenter.unbind();
  }

  @Override public void onDestroy() {
    super.onDestroy();
    Timber.d("onDestroy");
    presenter.destroy();
  }

  private Dialog init() {
    presenter.bind(this);
    @SuppressLint("InflateParams") final View rootView =
        LayoutInflater.from(getActivity()).inflate(R.layout.dialog_support, null, false);

    final Button aboutApp = (Button) rootView.findViewById(R.id.support_about_app);
    aboutApp.setOnClickListener(view -> presenter.clickAppPage(packageName));

    final TextView oneTitle = (TextView) rootView.findViewById(R.id.support_one_text);
    final TextView twoTitle = (TextView) rootView.findViewById(R.id.support_two_text);
    final TextView fiveTitle = (TextView) rootView.findViewById(R.id.support_five_text);
    final TextView tenTitle = (TextView) rootView.findViewById(R.id.support_ten_text);

    oneTitle.setOnClickListener(this);
    twoTitle.setOnClickListener(this);
    fiveTitle.setOnClickListener(this);
    tenTitle.setOnClickListener(this);

    final ImageView googlePlay = (ImageView) rootView.findViewById(R.id.google_play);
    final ImageView googlePlus = (ImageView) rootView.findViewById(R.id.google_plus);
    final ImageView blogger = (ImageView) rootView.findViewById(R.id.blogger);
    final ImageView facebook = (ImageView) rootView.findViewById(R.id.facebook);

    googlePlay.setOnClickListener(view1 -> presenter.clickGooglePlay());
    googlePlus.setOnClickListener(view1 -> presenter.clickGooglePlus());
    blogger.setOnClickListener(view1 -> presenter.clickBlogger());
    facebook.setOnClickListener(view1 -> presenter.clickFacebook());

    setDonationText(oneTitle, "Normal Donation", "Any little bit helps.");
    setDonationText(twoTitle, "Generous Donation", "This will help me out a lot.");
    setDonationText(fiveTitle, "Large Donation", "Be awesome today.");
    setDonationText(tenTitle, "Gigantic Donation", "Maximum awesomeness achieved.");

    return new AlertDialog.Builder(getActivity()).setNegativeButton("Later",
        (dialogInterface, i) -> {
          dialogInterface.dismiss();
        }).setView(rootView).create();
  }

  private void setDonationText(final TextView textView, final String title,
      final String description) {
    final Spannable spannable = StringUtil.createBuilder(title, "\n", description);
    final int largeLength = title.length();
    final int fullLength = largeLength + description.length();
    final int largeSize =
        StringUtil.getTextSizeFromAppearance(getContext(), android.R.attr.textAppearanceLarge);
    final int largeColor =
        StringUtil.getTextColorFromAppearance(getContext(), android.R.attr.textAppearanceLarge);
    final int smallSize =
        StringUtil.getTextSizeFromAppearance(getContext(), android.R.attr.textAppearanceSmall);
    final int smallColor =
        StringUtil.getTextColorFromAppearance(getContext(), android.R.attr.textAppearanceSmall);

    if (largeSize != 0 && largeColor != 0) {
      StringUtil.sizeSpan(spannable, 0, largeLength, largeSize);
      StringUtil.colorSpan(spannable, 0, largeLength, largeColor);
    }

    if (smallSize != 0 && smallColor != 0) {
      StringUtil.sizeSpan(spannable, largeLength + 1, fullLength, smallSize);
      StringUtil.colorSpan(spannable, largeLength + 1, fullLength, smallColor);
    }
    textView.setText(spannable);
  }

  private void onLinkClicked(final String link) {
    NetworkUtil.newLink(getContext(), link);
  }

  @Override public void onGooglePlayClicked(String link) {
    onLinkClicked(link);
  }

  @Override public void onGooglePlusClicked(String link) {
    onLinkClicked(link);
  }

  @Override public void onBloggerClicked(String link) {
    onLinkClicked(link);
  }

  @Override public void onFacebookClicked(String link) {
    onLinkClicked(link);
  }

  @Override public void onAppPageClicked(String link) {
    onLinkClicked(link);
  }

  @Override public void onClick(View view) {
    final int id = view.getId();
    String sku = null;
    if (id == R.id.support_one_text) {
      sku = APP_SKU_DONATE_ONE;
    } else if (id == R.id.support_two_text) {
      sku = APP_SKU_DONATE_TWO;
    } else if (id == R.id.support_five_text) {
      sku = APP_SKU_DONATE_FIVE;
    } else if (id == R.id.support_ten_text) {
      sku = APP_SKU_DONATE_TEN;
    }

    if (sku != null) {
      Timber.d("Attempt purchase of SKU: %s", sku);
      final FragmentActivity activity = getActivity();
      if (activity instanceof ActivityBase) {
        final ActivityBase activityBase = (ActivityBase) activity;
        activityBase.purchase(sku);
      }
    } else {
      Timber.e("SKU is null");
    }
  }
}
