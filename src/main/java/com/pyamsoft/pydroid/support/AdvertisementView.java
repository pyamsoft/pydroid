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
import android.support.annotation.CheckResult;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.pyamsoft.pydroid.R;
import com.pyamsoft.pydroid.model.AsyncDrawable;
import com.pyamsoft.pydroid.tool.AsyncDrawableTask;
import com.pyamsoft.pydroid.tool.AsyncTaskMap;
import com.pyamsoft.pydroid.tool.AsyncVectorDrawableTask;
import com.pyamsoft.pydroid.util.AppUtil;
import com.pyamsoft.pydroid.util.NetworkUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import timber.log.Timber;

public final class AdvertisementView extends FrameLayout {

  @NonNull private static final String ADVERTISEMENT_SHOWN_COUNT_KEY = "advertisement_shown_count";
  @NonNull private static final String PACKAGE_PASTERINO = "com.pyamsoft.pasterino";
  @NonNull private static final String PACKAGE_PADLOCK = "com.pyamsoft.padlock";
  @NonNull private static final String PACKAGE_POWERMANAGER = "com.pyamsoft.powermanager";
  @NonNull private static final String[] POSSIBLE_PACKAGES = {
      PACKAGE_PASTERINO, PACKAGE_PADLOCK, PACKAGE_POWERMANAGER
  };
  @NonNull private final AsyncTaskMap taskMap = new AsyncTaskMap();
  private Queue<String> imageQueue;
  private boolean preferenceDefault;
  private String preferenceKey;
  private ImageView advertisement;
  private ImageView closeButton;

  public AdvertisementView(Context context) {
    this(context, null);
  }

  public AdvertisementView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public AdvertisementView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public AdvertisementView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    Timber.d("Init with defStyleRes");
    init();
  }

  private void init() {
    Timber.d("Init advertisement view");
    preferenceKey = getContext().getString(R.string.adview_key);
    preferenceDefault = getContext().getResources().getBoolean(R.bool.adview_default);

    // Randomize the order of items
    final List<String> randomList = new ArrayList<>(Arrays.asList(POSSIBLE_PACKAGES));
    Collections.shuffle(randomList, new Random(System.nanoTime()));
    imageQueue = new LinkedList<>(randomList);

    ViewCompat.setElevation(this, AppUtil.convertToDP(getContext(), 2));
    inflate(getContext(), R.layout.view_advertisement, this);
  }

  public final void create() {
    create(0);
  }

  public final void create(@ColorInt int color) {
    advertisement = (ImageView) findViewById(R.id.ad_image);
    closeButton = (ImageView) findViewById(R.id.ad_close);

    Timber.d("Async load close button");
    AsyncVectorDrawableTask closeTask;
    if (color == 0) {
      Timber.d("Default color is black");
      closeTask = new AsyncVectorDrawableTask(closeButton, android.R.color.black);
    } else {
      Timber.d("Override color");
      closeTask = new AsyncVectorDrawableTask(closeButton, color);
    }
    closeTask.execute(new AsyncDrawable(getContext(), R.drawable.ic_close_24dp));
    taskMap.put("close", closeTask);

    closeButton.setOnClickListener(view -> {
      Timber.d("Close clicked");
      hide();
    });

    // Default to gone
    hide();
  }

  public final void destroy() {
    taskMap.clear();
  }

  public final void hide() {
    Timber.d("Hide adView");
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
        image = R.drawable.feature_pasterino;
        break;
      default:
        Timber.e("Invalid feature: %s", currentPackage);
        throw new IllegalStateException("Invalid feature: " + currentPackage);
    }

    return image;
  }

  public final void show(boolean force) {
    // KLUDGE: Direct preference object access and modify
    final SharedPreferences preferences =
        PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext());
    final boolean isEnabled = force || preferences.getBoolean(preferenceKey, preferenceDefault);
    final int shownCount = preferences.getInt(ADVERTISEMENT_SHOWN_COUNT_KEY, 0);
    final boolean isValidCount = shownCount == 4;
    if (isEnabled && isValidCount) {
      Timber.d("Write shown count back to 0");
      preferences.edit().putInt(ADVERTISEMENT_SHOWN_COUNT_KEY, 0).apply();
      Timber.d("Show ad view");
      setVisibility(View.VISIBLE);
      final AsyncDrawableTask adTask = new AsyncDrawableTask(advertisement);
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
      final int image = loadImage(currentPackage);

      final String clickPackage = currentPackage;
      advertisement.setOnClickListener(view -> {
        // KLUDGE: Social Media presenter can do this
        Timber.d("onClick");
        final String fullLink = "market://details?id=" + clickPackage;
        NetworkUtil.newLink(view.getContext(), fullLink);
      });

      adTask.execute(new AsyncDrawable(getContext(), image));
      taskMap.put("ad", adTask);
    } else {
      final int newCount = shownCount + 1;
      Timber.d("Increment shown count to %d", newCount);
      preferences.edit().putInt(ADVERTISEMENT_SHOWN_COUNT_KEY, newCount).apply();
    }
  }
}
