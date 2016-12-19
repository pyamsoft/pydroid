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

package com.pyamsoft.pydroidui.sec;

import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import com.pyamsoft.pydroid.SocialMediaLoaderCallback;
import com.pyamsoft.pydroid.social.SocialMediaPresenter;
import com.pyamsoft.pydroid.util.NetworkUtil;
import com.pyamsoft.pydroid.util.PersistentCache;
import com.pyamsoft.pydroidui.R;

public class TamperDialog extends DialogFragment implements SocialMediaPresenter.View {

  @NonNull private static final String KEY_SOCIAL_MEDIA = "__key_social_media_tamper";
  @SuppressWarnings("WeakerAccess") SocialMediaPresenter presenter;
  private long loadedKey;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    loadedKey = PersistentCache.get()
        .load(KEY_SOCIAL_MEDIA, savedInstanceState, new SocialMediaLoaderCallback() {

          @Override public void onPersistentLoaded(@NonNull SocialMediaPresenter persist) {
            presenter = persist;
          }
        });

    setCancelable(false);
  }

  @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
    return new AlertDialog.Builder(getActivity()).setTitle(
        "WARNING: THIS APPLICATION IS NOT OFFICIAL")
        .setMessage(R.string.tamper_msg)
        .setCancelable(false)
        .setPositiveButton("Take Me", (dialogInterface, i) -> presenter.clickGooglePlay())
        .setNegativeButton("Close", (dialogInterface, i) -> killApp())
        .create();
  }

  /**
   * Kills the app and clears the data to prevent any malicious services or code from possibly
   * running in the background
   */
  @SuppressWarnings("WeakerAccess") void killApp() {
    dismiss();
    getActivity().finish();
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      final ActivityManager activityManager = (ActivityManager) getContext().getApplicationContext()
          .getSystemService(Context.ACTIVITY_SERVICE);
      activityManager.clearApplicationUserData();
    }
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    PersistentCache.get()
        .saveKey(outState, KEY_SOCIAL_MEDIA, loadedKey, SocialMediaPresenter.class);
    super.onSaveInstanceState(outState);
  }

  @Override public void onDestroy() {
    super.onDestroy();
    if (!getActivity().isChangingConfigurations()) {
      PersistentCache.get().unload(loadedKey);
    }
  }

  @Override public void onStart() {
    super.onStart();
    presenter.bindView(this);
  }

  @Override public void onStop() {
    super.onStop();
    presenter.unbindView();
  }

  @Override public void onSocialMediaClicked(@NonNull String link) {
    NetworkUtil.newLink(getContext(), link);
    killApp();
  }
}
