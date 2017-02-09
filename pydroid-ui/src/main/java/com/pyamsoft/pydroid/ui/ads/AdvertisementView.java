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
import android.view.View;
import android.widget.FrameLayout;
import com.pyamsoft.pydroid.ads.AdSource;
import com.pyamsoft.pydroid.ads.AdvertisementPresenter;
import com.pyamsoft.pydroid.ui.PYDroidInjector;
import com.pyamsoft.pydroid.util.AppUtil;
import com.pyamsoft.pydroid.util.NetworkUtil;
import timber.log.Timber;

public class AdvertisementView extends FrameLayout {

  @NonNull private final AdSource offlineAdSource = new OfflineAdSource();
  @SuppressWarnings("WeakerAccess") Handler handler;
  @SuppressWarnings("WeakerAccess") AdvertisementPresenter presenter;
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

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
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
    PYDroidInjector.get().provideComponent().provideAdvertisementComponent().inject(this);

    // Default to gone
    setVisibility(View.GONE);

    addView(offlineAdSource.create(activity));

    onlineAdSource = adSource;
    if (onlineAdSource != null) {
      addView(onlineAdSource.create(activity));
    }
  }

  public final void start() {
    Timber.d("Start adView");
    presenter.bindView(null);
    showAd();

    offlineAdSource.start();
    if (onlineAdSource != null) {
      onlineAdSource.start();
    }
  }

  public final void stop() {
    Timber.d("Stop adView");
    presenter.unbindView();
    hideAd();

    handler.removeCallbacksAndMessages(null);

    offlineAdSource.stop();
    if (onlineAdSource != null) {
      onlineAdSource.stop();
    }
  }

  public final void destroy(@NonNull FragmentActivity activity, boolean isChangingConfigurations) {
    runOnAdHidden();

    removeView(offlineAdSource.destroy(activity, isChangingConfigurations));
    if (onlineAdSource != null) {
      removeView(onlineAdSource.destroy(activity, isChangingConfigurations));
    }
  }

  public final void showAd() {
    presenter.showAd(() -> {
      Timber.d("Show ad view");
      setVisibility(View.VISIBLE);
      queueAdRefresh();
    });
  }

  public final void hideAd() {
    presenter.hideAd(this::runOnAdHidden);
  }

  void runOnAdHidden() {
    Timber.d("Hide ad view");
    setVisibility(View.GONE);

    offlineAdSource.hideAd();
    if (onlineAdSource != null) {
      onlineAdSource.hideAd();
    }
  }

  @SuppressWarnings("WeakerAccess") void queueAdRefresh() {
    if (onlineAdSource != null && NetworkUtil.hasConnection(getContext())) {
      onlineAdSource.showAd();
    } else {
      offlineAdSource.showAd();
    }

    Timber.d("Post new ad in 60 seconds");
    handler.postDelayed(this::queueAdRefresh, 60 * 1000L);
  }
}
