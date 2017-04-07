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

package com.pyamsoft.pydroid.ui.sec;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.pyamsoft.pydroid.helper.BuildConfigChecker;
import com.pyamsoft.pydroid.ui.rating.RatingActivity;
import com.pyamsoft.pydroid.util.DialogUtil;
import timber.log.Timber;

public abstract class TamperActivity extends RatingActivity {

  private static final String GOOGLE_PLAY_STORE_INSTALLER = "com.android.vending";
  private boolean hasBeenTamperedWith;

  @CallSuper @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    hasBeenTamperedWith = checkNotTamperedWith();
  }

  /**
   * Returns true if the application has been tampered with, false if not
   */
  @CheckResult private boolean checkNotTamperedWith() {
    final String safePackageName = getSafePackageName();

    // Check if we are renamed
    if (getApplicationContext().getPackageName().compareTo(safePackageName) != 0) {
      Timber.e("Application is potentially re-named");
      return true;
    }

    // Check that we were installed from the play store.
    final String installer =
        getApplicationContext().getPackageManager().getInstallerPackageName(safePackageName);
    if (BuildConfigChecker.getInstance().isDebugMode()) {
      if (installer == null) {
        Timber.i("Application is installed from APK. This is fine in DEBUG mode");
        return false;
      } else {
        Timber.e("DEBUG Application is not installed from APK");
        return true;
      }
    } else {
      if (installer == null) {
        Timber.e("RELEASE Application is not installed from Google Play Store");
        Timber.e("Installer: NULL");
        return true;
      }

      if (GOOGLE_PLAY_STORE_INSTALLER.compareTo(installer) != 0) {
        Timber.e("RELEASE Application is not installed from Google Play Store");
        Timber.e("Installer: %s", installer);
        return true;
      }

      Timber.d("Application is safe");
      return false;
    }
  }

  @Override protected void onPostResume() {
    super.onPostResume();
    if (hasBeenTamperedWith) {
      Timber.e("Application has been tampered with, notify user");
      DialogUtil.guaranteeSingleDialogFragment(this, new TamperDialog(), "tamper");
    }
  }

  /**
   * The package name that we expect to be running under
   */
  @CheckResult @NonNull protected abstract String getSafePackageName();
}
