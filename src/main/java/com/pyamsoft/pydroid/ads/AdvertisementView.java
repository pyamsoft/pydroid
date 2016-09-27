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

package com.pyamsoft.pydroid.ads;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.pyamsoft.pydroid.AdvertisementViewLoaderCallback;
import com.pyamsoft.pydroid.R;
import com.pyamsoft.pydroid.R2;
import com.pyamsoft.pydroid.util.AppUtil;
import com.pyamsoft.pydroid.util.AsyncDrawable;
import com.pyamsoft.pydroid.util.NetworkUtil;
import com.pyamsoft.pydroid.util.PersistentCache;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import timber.log.Timber;

public class AdvertisementView extends FrameLayout implements AdvertisementPresenter.AdView {

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
  @NonNull private static final String KEY_ADVERTISEMENT = "key_advertisement_presenter";

  @SuppressWarnings("WeakerAccess") @NonNull final Handler handler;
  @NonNull private final AsyncDrawable.Mapper taskMap = new AsyncDrawable.Mapper();
  @BindView(R2.id.ad_image) ImageView advertisement;
  AdvertisementPresenter presenter;
  private Unbinder unbinder;
  private Queue<String> imageQueue;
  private long loadedKey;

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
    // Randomize the order of items
    final List<String> randomList = new ArrayList<>(Arrays.asList(POSSIBLE_PACKAGES));
    Collections.shuffle(randomList, new Random(System.nanoTime()));
    imageQueue = new LinkedList<>(randomList);

    ViewCompat.setElevation(this, AppUtil.convertToDP(getContext(), 2));
    inflate(getContext(), R.layout.view_advertisement, this);
  }

  @SuppressWarnings("WeakerAccess") public final void create() {
    unbinder = ButterKnife.bind(this, this);

    loadedKey = PersistentCache.get()
        .load(KEY_ADVERTISEMENT, null, new AdvertisementViewLoaderCallback(getContext()) {

          @Override public void onPersistentLoaded(@NonNull AdvertisementPresenter persist) {
            presenter = persist;
          }
        });

    // Default to gone
    setVisibility(View.GONE);
  }

  final void start() {
    Timber.d("Start adView");
    presenter.bindView(this);
  }

  final void stop() {
    Timber.d("Stop adView");
    presenter.unbindView();
    handler.removeCallbacksAndMessages(null);
  }

  final void destroy(boolean isChangingConfigurations) {
    taskMap.clear();
    advertisement.setImageDrawable(null);
    unbinder.unbind();

    if (!isChangingConfigurations) {
      PersistentCache.get().unload(loadedKey);
    }
  }

  public final void showAd() {
    if (presenter.isBound()) {
      presenter.showAd();
    }
  }

  public final void hideAd() {
    if (presenter.isBound()) {
      presenter.hideAd();
    }
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

  @Override public void onShown() {
    Timber.d("Show ad view");
    setVisibility(View.VISIBLE);
    showAdView();
  }

  @Override public void onHidden() {
    Timber.d("Hide ad view");
    setVisibility(View.GONE);
  }

  @SuppressWarnings("WeakerAccess") void showAdView() {
    advertisement.setVisibility(View.VISIBLE);

    final String currentPackage = currentPackageFromQueue();
    final int image = loadImage(currentPackage);
    advertisement.setOnClickListener(view -> presenter.clickAd(currentPackage));

    final AsyncTask adTask = AsyncDrawable.with(getContext()).load(image).into(advertisement);
    taskMap.put("ad", adTask);

    Timber.d("Post new ad in 60 seconds");
    handler.postDelayed(this::showAdView, 60 * 1000L);
  }

  @Override public void onSocialMediaClicked(@NonNull String link) {
    NetworkUtil.newLink(getContext(), link);
  }
}
