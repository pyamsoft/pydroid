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

package com.pyamsoft.pydroid.ui.rating;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewCompat;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import com.pyamsoft.pydroid.PYDroidPreferences;
import com.pyamsoft.pydroid.drawable.AsyncDrawable;
import com.pyamsoft.pydroid.drawable.AsyncMapEntry;
import com.pyamsoft.pydroid.helper.AsyncMapHelper;
import com.pyamsoft.pydroid.helper.Checker;
import com.pyamsoft.pydroid.ui.databinding.DialogRatingBinding;
import com.pyamsoft.pydroid.util.AppUtil;
import com.pyamsoft.pydroid.util.NetworkUtil;
import timber.log.Timber;

public class RatingDialog extends DialogFragment {

  @NonNull private static final String CHANGE_LOG_TEXT = "change_log_text";
  @NonNull private static final String CHANGE_LOG_ICON = "change_log_icon";
  @NonNull private static final String VERSION_CODE = "version_code";
  @NonNull private static final String RATE_LINK = "rate_link";
  @SuppressWarnings("WeakerAccess") String rateLink;
  @SuppressWarnings("WeakerAccess") boolean acknowledged;
  private Spannable changeLogText;
  private int versionCode;
  @DrawableRes private int changeLogIcon;
  private DialogRatingBinding binding;
  @Nullable private AsyncMapEntry iconTask;

  public static void showRatingDialog(@NonNull FragmentActivity activity,
      @NonNull ChangeLogProvider provider, final boolean force) {
    activity = Checker.checkNonNull(activity);
    provider = Checker.checkNonNull(provider);

    final PYDroidPreferences preferences = PYDroidPreferences.Instance.getInstance(activity);
    if (force || preferences.getRatingAcceptedVersion() < provider.getCurrentApplicationVersion()) {
      AppUtil.onlyLoadOnceDialogFragment(activity, newInstance(provider), "rating");
    }
  }

  @CheckResult @NonNull
  private static RatingDialog newInstance(@NonNull ChangeLogProvider provider) {
    provider = Checker.checkNonNull(provider);

    final RatingDialog fragment = new RatingDialog();
    final Bundle args = new Bundle();
    args.putString(RATE_LINK, provider.getPackageName());
    args.putCharSequence(CHANGE_LOG_TEXT, provider.getChangeLogText());
    args.putInt(VERSION_CODE, provider.getCurrentApplicationVersion());
    args.putInt(CHANGE_LOG_ICON, provider.getApplicationIcon());
    fragment.setArguments(args);
    return fragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setCancelable(false);
    acknowledged = false;
    final Bundle launchArguments = getArguments();
    rateLink = launchArguments.getString(RATE_LINK, null);
    versionCode = launchArguments.getInt(VERSION_CODE, 0);
    changeLogText = (Spannable) launchArguments.getCharSequence(CHANGE_LOG_TEXT, null);
    changeLogIcon = launchArguments.getInt(CHANGE_LOG_ICON, 0);

    if (versionCode == 0) {
      throw new RuntimeException("Version code cannot be 0");
    }

    if (changeLogText == null) {
      throw new RuntimeException("Change Log text cannot be NULL");
    }

    if (changeLogIcon == 0) {
      throw new RuntimeException("Change Log Icon Id cannot be 0");
    }
  }

  @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
    final Dialog dialog = super.onCreateDialog(savedInstanceState);
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    return dialog;
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    iconTask = AsyncMapHelper.unsubscribe(iconTask);
    binding.unbind();
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    binding = DialogRatingBinding.inflate(inflater, container, false);
    return binding.getRoot();
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    initDialog();
  }

  private void initDialog() {
    ViewCompat.setElevation(binding.ratingIcon, AppUtil.convertToDP(getContext(), 8));

    iconTask = AsyncMapHelper.unsubscribe(iconTask);
    iconTask = AsyncDrawable.load(changeLogIcon).into(binding.ratingIcon);

    binding.ratingTextChange.setText(changeLogText);

    binding.ratingBtnNoThanks.setOnClickListener(v -> {
      acknowledged = true;
      dismiss();
    });

    binding.ratingBtnGoRate.setOnClickListener(v -> {
      acknowledged = true;
      final String fullLink = "market://details?id=" + rateLink;
      NetworkUtil.newLink(v.getContext().getApplicationContext(), fullLink);
      dismiss();
    });
  }

  @Override public void onDismiss(DialogInterface dialog) {
    super.onDismiss(dialog);
    if (acknowledged) {
      Timber.d("Rating dialog has been addressed by user. Commit to memory");
      PYDroidPreferences.Instance.getInstance(getContext()).setRatingAcceptedVersion(versionCode);
    }
  }

  @Override public void onResume() {
    super.onResume();
    // The dialog is super small for some reason. We have to set the size manually, in onResume
    final Window window = getDialog().getWindow();
    if (window != null) {
      window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
          WindowManager.LayoutParams.WRAP_CONTENT);
    }
  }

  public interface ChangeLogProvider {

    @CheckResult @NonNull Spannable getChangeLogText();

    @DrawableRes @CheckResult int getApplicationIcon();

    @CheckResult @NonNull String getPackageName();

    @CheckResult int getCurrentApplicationVersion();
  }
}
