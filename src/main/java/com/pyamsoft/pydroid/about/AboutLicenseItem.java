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

package com.pyamsoft.pydroid.about;

import android.content.Context;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;
import com.pyamsoft.pydroid.R;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

class AboutLicenseItem extends AbstractItem<AboutLicenseItem, AboutLicenseItem.ViewHolder> {

  @NonNull private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();
  @NonNull private final Licenses licenseName;

  AboutLicenseItem(@NonNull Licenses licenseName) {
    this.licenseName = licenseName;
  }

  @Override public int getType() {
    return R.id.fastadapter_about_license_item;
  }

  @Override public int getLayoutRes() {
    return R.layout.adapter_item_license;
  }

  @Override public ViewHolderFactory<? extends ViewHolder> getFactory() {
    return FACTORY;
  }

  @VisibleForTesting @NonNull @CheckResult String loadLicenseText(@NonNull Context context,
      @NonNull Licenses license) {
    String licenseText;
    final Context appContext = context.getApplicationContext();
    final StringBuilder text = new StringBuilder();
    final String licenseFileName = getLicenseFileName(license);
    try (
        final InputStream fileInputStream = appContext.getAssets().open(licenseFileName);
        final BufferedReader br = new BufferedReader(new InputStreamReader(fileInputStream))) {
      String line = br.readLine();
      while (line != null) {
        text.append(line).append('\n');
        line = br.readLine();
      }
      licenseText = text.toString();
    } catch (IOException e) {
      e.printStackTrace();
      licenseText = "Could not load license text";
    }

    return licenseText;
  }

  @NonNull @VisibleForTesting @CheckResult String getLicenseFileName(@NonNull Licenses license) {
    final String fileLocation;
    switch (license) {
      case ANDROID:
        fileLocation = "licenses/android_20160903.txt";
        break;
      case ANDROID_SUPPORT:
        fileLocation = "licenses/android_support_20160903.txt";
        break;
      default:
        throw new RuntimeException("Invalid license type: " + license.name());
    }
    return fileLocation;
  }

  @Override public void bindView(ViewHolder holder, List payloads) {
    super.bindView(holder, payloads);
    holder.licenseText.setText(loadLicenseText(holder.itemView.getContext(), licenseName));
  }

  protected static class ItemFactory implements ViewHolderFactory<ViewHolder> {

    @Override public ViewHolder create(@NonNull View v) {
      return new ViewHolder(v);
    }
  }

  protected static class ViewHolder extends RecyclerView.ViewHolder {

    final TextView licenseText;

    public ViewHolder(View itemView) {
      super(itemView);
      licenseText = (TextView) itemView.findViewById(R.id.about_license_text);
    }
  }
}
