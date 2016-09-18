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

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.pyamsoft.pydroid.BuildConfig;
import com.pyamsoft.pydroid.R;
import com.pyamsoft.pydroid.R2;
import com.pyamsoft.pydroid.tool.AsyncDrawable;
import com.pyamsoft.pydroid.tool.AsyncDrawableMap;
import com.pyamsoft.pydroid.util.AppUtil;
import com.pyamsoft.pydroid.util.NetworkUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import rx.Subscription;
import timber.log.Timber;

public class AdvertisementView extends FrameLayout {

  @NonNull private static final String ADVERTISEMENT_SHOWN_COUNT_KEY = "advertisement_shown_count";
  @NonNull private static final String PACKAGE_PASTERINO = "com.pyamsoft.pasterino";
  @NonNull private static final String PACKAGE_PADLOCK = "com.pyamsoft.padlock";
  @NonNull private static final String PACKAGE_POWERMANAGER = "com.pyamsoft.powermanager";
  @NonNull private static final String PACKAGE_HOMEBUTTON = "com.pyamsoft.homebutton";
  @NonNull private static final String PACKAGE_ZAPTORCH = "com.pyamsoft.zaptorch";
  @NonNull private static final String PACKAGE_WORDWIZ = "com.pyamsoft.wordwiz";
  @NonNull private static final String[] POSSIBLE_PACKAGES = {
      PACKAGE_PASTERINO, PACKAGE_PADLOCK, PACKAGE_POWERMANAGER, PACKAGE_HOMEBUTTON,
      PACKAGE_ZAPTORCH, PACKAGE_WORDWIZ
  };

  private static final int MAX_SHOW_COUNT = 4;
  @SuppressWarnings("WeakerAccess") @NonNull final Handler handler;
  @NonNull private final AsyncDrawableMap taskMap = new AsyncDrawableMap();
  @BindView(R2.id.ad_image) ImageView advertisement;
  @SuppressWarnings("WeakerAccess") AdView realAdView;
  private Unbinder unbinder;
  private Queue<String> imageQueue;
  private boolean preferenceDefault;
  private String preferenceKey;

  public AdvertisementView(Context context) {
    super(context);
    handler = new Handler(Looper.getMainLooper());
    init();
  }

  public AdvertisementView(Context context, AttributeSet attrs) {
    super(context, attrs);
    handler = new Handler(Looper.getMainLooper());
    init();
  }

  public AdvertisementView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    handler = new Handler(Looper.getMainLooper());
    init();
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public AdvertisementView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    handler = new Handler(Looper.getMainLooper());
    init();
  }

  private void init() {
    preferenceKey = getContext().getString(R.string.adview_key);
    preferenceDefault = getContext().getResources().getBoolean(R.bool.adview_default);

    // Randomize the order of items
    final List<String> randomList = new ArrayList<>(Arrays.asList(POSSIBLE_PACKAGES));
    Collections.shuffle(randomList, new Random(System.nanoTime()));
    imageQueue = new LinkedList<>(randomList);

    ViewCompat.setElevation(this, AppUtil.convertToDP(getContext(), 2));
    inflate(getContext(), R.layout.view_advertisement, this);
  }

  @SuppressWarnings("WeakerAccess") public final void create(@NonNull final String adId) {
    Timber.d("Create AdView with debug mode: %s", BuildConfig.DEBUG);

    unbinder = ButterKnife.bind(this, this);

    // Setup real ad view
    setupRealAdView(adId);

    // Default to gone
    setVisibility(View.GONE);
  }

  public final void start() {
    Timber.d("Start adView");
    show();
  }

  public final void resume() {
    Timber.d("Resume adView");
    realAdView.resume();
  }

  public final void pause() {
    Timber.d("Pause adView");
    realAdView.pause();
  }

  public final void stop() {
    Timber.d("Stop adView");
    handler.removeCallbacksAndMessages(null);
  }

  public final void destroy() {
    Timber.d("Destroy AdView");
    final SharedPreferences preferences =
        PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext());
    if (preferences.getInt(ADVERTISEMENT_SHOWN_COUNT_KEY, 0) >= MAX_SHOW_COUNT) {
      Timber.d("Write shown count back to 0");
      preferences.edit().putInt(ADVERTISEMENT_SHOWN_COUNT_KEY, 0).apply();
    }

    taskMap.clear();
    advertisement.setImageDrawable(null);
    realAdView.removeAllViews();
    realAdView.setAdListener(null);
    realAdView.destroy();
    removeView(realAdView);
    unbinder.unbind();
  }

  private void setupRealAdView(@NonNull final String adId) {
    realAdView = new AdView(getContext().getApplicationContext());
    realAdView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT));
    realAdView.setAdSize(AdSize.SMART_BANNER);
    realAdView.setAdUnitId(adId);
    realAdView.setAdListener(new AdListener() {

      @Override public void onAdLoaded() {
        super.onAdLoaded();
        advertisement.setVisibility(View.GONE);
        realAdView.setVisibility(View.VISIBLE);
      }

      @Override public void onAdFailedToLoad(int i) {
        super.onAdFailedToLoad(i);
        showAdViewNoNetwork();

        Timber.d("Post another load attempt in 60 seconds");
        handler.postDelayed(AdvertisementView.this::showAdView, 60000);
      }
    });

    // Init mobile Ads
    MobileAds.initialize(getContext().getApplicationContext(), adId);
    addView(realAdView, 0);
  }

  public final void hide() {
    final SharedPreferences preferences =
        PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext());
    Timber.d("Write shown count back to 0");
    preferences.edit().putInt(ADVERTISEMENT_SHOWN_COUNT_KEY, 0).apply();

    Timber.d("Hide ad view");
    setVisibility(View.GONE);
  }

  @CheckResult private int loadImage(@NonNull String currentPackage) {
    int image;
    switch (currentPackage) {
      case PACKAGE_PADLOCK:
        Timber.d("Load feature: PadLock");
        image = R.drawable.feature_padlock;
        break;
      case PACKAGE_PASTERINO:
        Timber.d("Load feature: Pasterino");
        image = R.drawable.feature_pasterino;
        break;
      case PACKAGE_POWERMANAGER:
        Timber.d("Load feature: Power Manager");
        image = R.drawable.feature_powermanager;
        break;
      case PACKAGE_HOMEBUTTON:
        Timber.d("Load feature: Home Button");
        image = R.drawable.feature_homebutton;
        break;
      case PACKAGE_ZAPTORCH:
        Timber.d("Load feature: ZapTorch");
        image = R.drawable.feature_zaptorch;
        break;
      case PACKAGE_WORDWIZ:
        Timber.d("Load feature: WordWiz");
        image = R.drawable.feature_wordwiz;
        break;
      default:
        Timber.e("Invalid feature: %s", currentPackage);
        throw new IllegalStateException("Invalid feature: " + currentPackage);
    }

    return image;
  }

  @CheckResult @NonNull private String currentPackageFromQueue() {
    String currentPackage = imageQueue.poll();
    while (currentPackage == null || currentPackage.equals(getContext().getPackageName())) {
      Timber.e("Current package is bad: %s", currentPackage);
      if (currentPackage != null) {
        Timber.d("Add non-null package back to queue");
        imageQueue.add(currentPackage);
      } else {
        Timber.d("Remove null package from queue");
      }

      Timber.d("Get new current package");
      currentPackage = imageQueue.poll();
    }

    imageQueue.add(currentPackage);
    Timber.d("Image queue: %s", Arrays.toString(imageQueue.toArray()));

    return currentPackage;
  }

  public void show() {
    // KLUDGE: Direct preference object access and modify
    final SharedPreferences preferences =
        PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext());
    final boolean isEnabled = preferences.getBoolean(preferenceKey, preferenceDefault);
    final int shownCount = preferences.getInt(ADVERTISEMENT_SHOWN_COUNT_KEY, 0);
    final boolean isValidCount = shownCount >= MAX_SHOW_COUNT;
    if (isEnabled && isValidCount) {
      Timber.d("Show ad view");
      setVisibility(View.VISIBLE);
      showAdView();
    } else {
      final int newCount = shownCount + 1;
      Timber.d("Increment shown count to %d", newCount);
      preferences.edit().putInt(ADVERTISEMENT_SHOWN_COUNT_KEY, newCount).apply();
    }
  }

  @SuppressWarnings("WeakerAccess") void showAdView() {
    showAdViewNoNetwork();
    if (NetworkUtil.hasConnection(getContext())) {
      showAdViewNetwork();
    }
  }

  private void showAdViewNetwork() {
    final AdRequest.Builder builder = new AdRequest.Builder();
    if (BuildConfig.DEBUG) {
      builder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
      builder.addTestDevice(getContext().getString(R.string.test_id_1));
    }

    final AdRequest adRequest = builder.build();
    realAdView.loadAd(adRequest);
  }

  @SuppressWarnings("WeakerAccess") void showAdViewNoNetwork() {
    realAdView.setVisibility(View.GONE);
    advertisement.setVisibility(View.VISIBLE);

    final String currentPackage = currentPackageFromQueue();
    final int image = loadImage(currentPackage);
    advertisement.setOnClickListener(view -> {
      // KLUDGE: Social Media presenter can do this
      Timber.d("onClick");
      final String fullLink = "market://details?id=" + currentPackage;
      NetworkUtil.newLink(view.getContext(), fullLink);
    });

    final Subscription adTask = AsyncDrawable.with(getContext()).load(image).into(advertisement);
    taskMap.put("ad", adTask);
  }
}
