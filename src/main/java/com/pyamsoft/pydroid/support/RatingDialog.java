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
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.CheckResult;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.pyamsoft.pydroid.R;
import com.pyamsoft.pydroid.tool.AsyncDrawable;
import com.pyamsoft.pydroid.tool.AsyncDrawableMap;
import com.pyamsoft.pydroid.util.AppUtil;
import com.pyamsoft.pydroid.util.NetworkUtil;
import rx.Subscription;
import timber.log.Timber;

public class RatingDialog extends DialogFragment {

  @NonNull private static final String PREFERENCE_TARGET = "rating_dialog_accepted_version";
  @NonNull private static final String CHANGE_LOG_TEXT = "change_log_text";
  @NonNull private static final String CHANGE_LOG_ICON = "change_log_icon";
  @NonNull private static final String VERSION_CODE = "version_code";
  @NonNull private static final String RATE_LINK = "rate_link";
  @NonNull private final AsyncDrawableMap taskMap = new AsyncDrawableMap();
  @SuppressWarnings("WeakerAccess") String rateLink;
  @SuppressWarnings("WeakerAccess") boolean acknowledged;
  private SharedPreferences preferences;
  private Spannable changeLogText;
  private int versionCode;
  @DrawableRes private int changeLogIcon;

  public static void showRatingDialog(final @NonNull FragmentActivity activity,
      final @NonNull ChangeLogProvider provider) {
    showRatingDialog(activity, provider, false);
  }

  // KLUDGE direct preference access
  public static void showRatingDialog(final @NonNull FragmentActivity activity,
      final @NonNull ChangeLogProvider provider, final boolean force) {
    final SharedPreferences preferences =
        PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
    if (force || preferences.getInt(PREFERENCE_TARGET, 0) < provider.getChangeLogVersion()) {
      AppUtil.guaranteeSingleDialogFragment(activity, newInstance(provider), "rating");
    }
  }

  @CheckResult @NonNull
  private static RatingDialog newInstance(final @NonNull ChangeLogProvider provider) {
    final RatingDialog fragment = new RatingDialog();
    final Bundle args = new Bundle();
    args.putString(RATE_LINK, provider.getChangeLogPackageName());
    args.putCharSequence(CHANGE_LOG_TEXT, provider.getChangeLogText());
    args.putInt(VERSION_CODE, provider.getChangeLogVersion());
    args.putInt(CHANGE_LOG_ICON, provider.getChangeLogIcon());
    fragment.setArguments(args);
    return fragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setCancelable(false);
    acknowledged = false;
    preferences =
        PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext());
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

  @Override public void onDestroyView() {
    super.onDestroyView();
    taskMap.clear();
  }

  @SuppressLint("InflateParams") @NonNull @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

    final View rootView =
        LayoutInflater.from(getActivity()).inflate(R.layout.dialog_rating, null, false);
    final ImageView icon = (ImageView) rootView.findViewById(R.id.rating_icon);
    final TextView changeLog = (TextView) rootView.findViewById(R.id.rating_text_change);
    final Button rateButton = (Button) rootView.findViewById(R.id.rating_btn_go_rate);
    final Button cancelButton = (Button) rootView.findViewById(R.id.rating_btn_no_thanks);

    ViewCompat.setElevation(icon, AppUtil.convertToDP(getContext(), 8));

    final Subscription iconTask = AsyncDrawable.with(getContext()).load(changeLogIcon).into(icon);
    taskMap.put("icon", iconTask);

    changeLog.setText(changeLogText);
    builder.setView(rootView);

    cancelButton.setOnClickListener(v -> {
      acknowledged = true;
      dismiss();
    });

    rateButton.setOnClickListener(v -> {
      acknowledged = true;
      final String fullLink = "market://details?id=" + rateLink;
      NetworkUtil.newLink(v.getContext().getApplicationContext(), fullLink);
      dismiss();
    });

    return builder.create();
  }

  @Override public void onDismiss(DialogInterface dialog) {
    super.onDismiss(dialog);
    if (preferences != null && acknowledged) {
      Timber.d("Rating dialog has been addressed by user. Commit to memory");
      preferences.edit().putInt(PREFERENCE_TARGET, versionCode).apply();
    }
  }

  public interface ChangeLogProvider {

    @CheckResult @NonNull Spannable getChangeLogText();

    @CheckResult int getChangeLogIcon();

    @CheckResult @NonNull String getChangeLogPackageName();

    @CheckResult int getChangeLogVersion();
  }
}
