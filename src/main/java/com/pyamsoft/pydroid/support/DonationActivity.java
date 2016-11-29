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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.pyamsoft.pydroid.R;
import com.pyamsoft.pydroid.SupportPresenterProvider;
import com.pyamsoft.pydroid.version.VersionCheckActivity;

public abstract class DonationActivity extends VersionCheckActivity {

  @SuppressWarnings("WeakerAccess") SupportPresenter supportPresenter;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Create presenter here, do not persist
    supportPresenter = new DonationSupportPresenterProvider(this).providePresenter();
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    supportPresenter.destroy();
  }

  @CallSuper @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    supportPresenter.onBillingResult(requestCode, resultCode, data);
  }

  @CallSuper @Override public boolean onCreateOptionsMenu(@NonNull Menu menu) {
    super.onCreateOptionsMenu(menu);
    final MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.main_support, menu);
    return true;
  }

  @CallSuper @Override public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    final int itemId = item.getItemId();
    boolean handled;
    if (itemId == R.id.menu_support) {
      SupportDialog.show(getSupportFragmentManager());
      handled = true;
    } else {
      handled = false;
    }
    return handled;
  }

  @CheckResult @NonNull SupportPresenter getSupportPresenter() {
    if (supportPresenter == null) {
      throw new IllegalStateException("SupportPresenter is NULL");
    }
    return supportPresenter;
  }

  static class DonationSupportPresenterProvider extends SupportPresenterProvider {

    @NonNull private final Activity activity;

    DonationSupportPresenterProvider(@NonNull Activity activity) {
      this.activity = activity;
    }

    @NonNull @Override protected Activity provideActivity() {
      return activity;
    }
  }
}
