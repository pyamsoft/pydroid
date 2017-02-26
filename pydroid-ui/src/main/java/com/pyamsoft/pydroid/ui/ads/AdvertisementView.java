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

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import com.pyamsoft.pydroid.ads.AdSource;
import com.pyamsoft.pydroid.util.AppUtil;
import com.pyamsoft.pydroid.util.NetworkUtil;
import timber.log.Timber;

public class AdvertisementView extends FrameLayout {

  @NonNull private final AdSource offlineAdSource = new OfflineAdSource();
  @SuppressWarnings("WeakerAccess") Handler handler;
  @Nullable private AdSource onlineAdSource;

  public AdvertisementView(Context context) {
    super(context);
    init();
  }

  public AdvertisementView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public AdvertisementView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  @SuppressWarnings("unused") @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public AdvertisementView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    init();
  }

  private void init() {
    handler = new Handler(Looper.getMainLooper());
    ViewCompat.setElevation(this, AppUtil.convertToDP(getContext(), 2));
  }

  @SuppressWarnings("WeakerAccess")
  public final void create(@NonNull FragmentActivity activity, @Nullable AdSource adSource) {
    addView(offlineAdSource.create(activity));

    onlineAdSource = adSource;
    if (onlineAdSource != null) {
      addView(onlineAdSource.create(activity));
    }
  }

  public final void start() {
    Timber.d("Start adView");
    offlineAdSource.start();
    if (onlineAdSource != null) {
      onlineAdSource.start();
    }

    queueAdRefresh();
  }

  public final void stop() {
    Timber.d("Stop adView");
    handler.removeCallbacksAndMessages(null);

    offlineAdSource.stop();
    if (onlineAdSource != null) {
      onlineAdSource.stop();
    }
  }

  public final void destroy(boolean isChangingConfigurations) {
    removeView(offlineAdSource.destroy(isChangingConfigurations));
    if (onlineAdSource != null) {
      removeView(onlineAdSource.destroy(isChangingConfigurations));
    }
  }

  void queueAdRefresh() {
    if (onlineAdSource != null && NetworkUtil.hasConnection(getContext())) {
      onlineAdSource.refreshAd(new AdSource.AdRefreshedCallback() {
        @Override public void onAdFailedLoad() {
          // Show offline ad if we fail
          refreshOfflineAd();
        }

        @Override public void onAdRefreshed() {
        }
      });
    } else {
      refreshOfflineAd();
    }

    Timber.d("Post new ad in 60 seconds");
    handler.removeCallbacksAndMessages(null);
    handler.postDelayed(this::queueAdRefresh, 60 * 1000L);
  }

  void refreshOfflineAd() {
    offlineAdSource.refreshAd(new AdSource.AdRefreshedCallback() {
      @Override public void onAdFailedLoad() {
      }

      @Override public void onAdRefreshed() {
      }
    });
  }
}
