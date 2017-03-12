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

package com.pyamsoft.pydroid.ui.ads;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.pyamsoft.pydroid.BuildConfigChecker;
import com.pyamsoft.pydroid.ads.AdSource;
import com.pyamsoft.pydroid.util.NetworkUtil;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import timber.log.Timber;

public class OnlineAdSource implements AdSource {

  @Nullable private final String adId;
  @StringRes private final int resAdId;
  @NonNull private final Set<String> testAdIds;
  @SuppressWarnings("WeakerAccess") AdView adView;
  boolean adHasLoaded;
  private AdRequest adRequest;

  public OnlineAdSource(@NonNull String adId) {
    this(adId, 0);
  }

  public OnlineAdSource(@StringRes int resAdId) {
    this(null, resAdId);
  }

  private OnlineAdSource(@Nullable String adId, @StringRes int resAdId) {
    this.adId = adId;
    this.resAdId = resAdId;
    testAdIds = new HashSet<>();
  }

  public void addTestAdIds(@NonNull String... testIds) {
    testAdIds.addAll(Arrays.asList(testIds));
  }

  @NonNull @Override public View create(@NonNull Context context) {
    adHasLoaded = false;
    adView = new AdView(context.getApplicationContext());
    adView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT));
    adView.setAdSize(AdSize.SMART_BANNER);
    adView.setVisibility(View.GONE);

    final String realAdId;
    if (adId == null) {
      realAdId = context.getApplicationContext().getString(resAdId);
    } else {
      realAdId = adId;
    }
    adView.setAdUnitId(realAdId);
    adView.setAdListener(null);

    AdRequest.Builder builder = new AdRequest.Builder();

    if (BuildConfigChecker.getInstance().isDebugMode()) {
      builder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
      //noinspection Convert2streamapi
      for (String testId : testAdIds) {
        builder.addTestDevice(testId);
      }
    }
    adRequest = builder.build();
    return adView;
  }

  @NonNull @Override public View destroy(boolean isChangingConfigurations) {
    adView.destroy();
    return adView;
  }

  @Override public void start() {
    adView.resume();
    if (!NetworkUtil.hasConnection(adView.getContext())) {
      adView.setVisibility(View.GONE);
    }
  }

  @Override public void stop() {
    adView.pause();
    if (!NetworkUtil.hasConnection(adView.getContext())) {
      adView.setVisibility(View.GONE);
    }
  }

  @Override public void refreshAd(@NonNull AdRefreshedCallback callback) {
    if (adView.getAdListener() == null) {
      adView.setAdListener(new AdListener() {
        @Override public void onAdLoaded() {
          super.onAdLoaded();
          adView.setVisibility(View.VISIBLE);
          callback.onAdRefreshed();
          adHasLoaded = true;
        }

        @Override public void onAdFailedToLoad(int i) {
          super.onAdFailedToLoad(i);
          Timber.e("Online Ad failed to load");
          adView.setVisibility(View.GONE);
          callback.onAdFailedLoad();
          adHasLoaded = false;
        }
      });
    }

    if (!adHasLoaded) {
      // Hide AdView until we have loaded
      adView.setVisibility(View.GONE);
    }
    adView.loadAd(adRequest);
  }
}
