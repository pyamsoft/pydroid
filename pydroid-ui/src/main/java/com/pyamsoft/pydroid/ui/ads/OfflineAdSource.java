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
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.pyamsoft.pydroid.ads.AdSource;
import com.pyamsoft.pydroid.helper.Checker;
import com.pyamsoft.pydroid.social.Linker;
import com.pyamsoft.pydroid.ui.R;
import com.pyamsoft.pydroid.ui.loader.DrawableHelper;
import com.pyamsoft.pydroid.ui.loader.DrawableLoader;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import timber.log.Timber;

public class OfflineAdSource implements AdSource {

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
  @SuppressWarnings("WeakerAccess") Context appContext;
  private Queue<String> imageQueue;
  private ImageView adImage;
  @NonNull private DrawableLoader.Loaded adTask = DrawableLoader.empty();

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
    if (imageQueue == null) {
      throw new IllegalStateException("No image queue exists, must create ad source first");
    }
    if (adImage == null) {
      throw new IllegalStateException("Cannot get current ad with non-existent AdImage");
    }

    final Context context = adImage.getContext();
    String currentPackage = imageQueue.poll();
    while (currentPackage == null || currentPackage.equals(context.getPackageName())) {
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

    return currentPackage;
  }

  @NonNull @Override public View create(@NonNull Context context) {
    appContext = Checker.checkNonNull(context).getApplicationContext();

    // Randomize the order of items
    final List<String> randomList = new ArrayList<>(Arrays.asList(POSSIBLE_PACKAGES));
    Collections.shuffle(randomList, new SecureRandom());
    imageQueue = new LinkedList<>(randomList);

    // Create Ad image in java to avoid inflation cost
    adImage = new ImageView(appContext);
    adImage.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT));
    adImage.setScaleType(ImageView.ScaleType.FIT_XY);
    return adImage;
  }

  @NonNull @Override public View destroy(boolean isChangingConfigurations) {
    adTask = DrawableHelper.unload(adTask);
    if (!isChangingConfigurations) {
      imageQueue.clear();
    }
    return adImage;
  }

  @Override public void start() {
  }

  @Override public void refreshAd(@NonNull AdRefreshedCallback refreshedCallback) {
    AdRefreshedCallback callback = Checker.checkNonNull(refreshedCallback);

    final String currentPackage = currentPackageFromQueue();
    final int image = loadImage(currentPackage);
    adImage.setOnClickListener(view -> {
      Linker.with(appContext).clickAppPage(currentPackage);
    });

    adTask = DrawableHelper.unload(adTask);
    adTask = DrawableLoader.load(image)
        .setErrorAction(item -> callback.onAdFailedLoad())
        .setCompleteAction(item -> callback.onAdRefreshed())
        .into(adImage);
  }

  @Override public void stop() {
    adImage.setOnClickListener(null);
    adImage.setImageDrawable(null);
  }
}
