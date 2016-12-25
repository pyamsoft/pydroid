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

package com.pyamsoft.ui.ads;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.pyamsoft.pydroid.SocialMediaLoaderCallback;
import com.pyamsoft.pydroid.ads.AdSource;
import com.pyamsoft.pydroid.social.SocialMediaPresenter;
import com.pyamsoft.pydroid.tool.AsyncDrawable;
import com.pyamsoft.pydroid.tool.AsyncMap;
import com.pyamsoft.pydroid.util.AppUtil;
import com.pyamsoft.pydroid.util.NetworkUtil;
import com.pyamsoft.pydroid.util.PersistentCache;
import com.pyamsoft.ui.R;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import timber.log.Timber;

public class OfflineAdSource implements AdSource, SocialMediaPresenter.View {

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
  @NonNull private static final String KEY_PRESENTER = "__key_offline_ad_presenter";
  @NonNull private final AsyncDrawable.Mapper taskMap = new AsyncDrawable.Mapper();
  @Nullable @SuppressWarnings("WeakerAccess") SocialMediaPresenter presenter;
  @Nullable private Queue<String> imageQueue;
  @Nullable private ImageView adImage;
  private long loadedKey;

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
      throw new IllegalStateException("Canot get current ad with non-existant AdImage");
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
    Timber.d("Image queue: %s", Arrays.toString(imageQueue.toArray()));

    return currentPackage;
  }

  @NonNull @Override
  public View create(@NonNull Context context, @Nullable Bundle savedInstanceState) {
    loadedKey = PersistentCache.get()
        .load(KEY_PRESENTER, savedInstanceState, new SocialMediaLoaderCallback() {
          @Override public void onPersistentLoaded(@NonNull SocialMediaPresenter persist) {
            presenter = persist;
          }
        });

    // Randomize the order of items
    final List<String> randomList = new ArrayList<>(Arrays.asList(POSSIBLE_PACKAGES));
    Collections.shuffle(randomList, new SecureRandom());
    imageQueue = new LinkedList<>(randomList);

    // Create Ad image in java to avoid inflation cost
    adImage = new ImageView(context);
    adImage.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
        (int) AppUtil.convertToDP(context, 50)));
    adImage.setScaleType(ImageView.ScaleType.FIT_XY);
    return adImage;
  }

  @NonNull @Override public View destroy(boolean isChangingConfigurations) {
    taskMap.clear();
    if (!isChangingConfigurations) {
      PersistentCache.get().unload(loadedKey);
      if (imageQueue != null) {
        imageQueue.clear();
      }
    }

    if (adImage == null) {
      throw new IllegalStateException("Cannot remove non-existent AdImage");
    } else {
      return adImage;
    }
  }

  @Override public void start() {
    if (presenter == null) {
      throw new IllegalStateException("NULL presenter");
    }
    presenter.bindView(this);
  }

  @Override public void stop() {
    if (presenter == null) {
      throw new IllegalStateException("NULL presenter");
    }
    presenter.unbindView();
  }

  @Override public void showAd() {
    if (adImage == null) {
      throw new IllegalStateException("Cannot show ad with non-existent AdImage");
    }

    final String currentPackage = currentPackageFromQueue();
    final int image = loadImage(currentPackage);
    adImage.setOnClickListener(view -> {
      if (presenter == null) {
        throw new IllegalStateException("Cannot click ad with non-existent presenter");
      } else {
        presenter.clickAppPage(currentPackage);
      }
    });

    final AsyncMap.Entry adTask = AsyncDrawable.load(image).into(adImage);
    taskMap.put("ad", adTask);
  }

  @Override public void hideAd() {
    if (adImage == null) {
      throw new IllegalStateException("Cannot hide non-existant AdImage");
    } else {
      adImage.setImageDrawable(null);
      adImage.setOnClickListener(null);
    }
  }

  @Override public void saveState(@NonNull Bundle outState) {
    PersistentCache.get().saveKey(outState, KEY_PRESENTER, loadedKey, SocialMediaPresenter.class);
  }

  @Override public void onSocialMediaClicked(@NonNull String link) {
    if (adImage == null) {
      throw new IllegalStateException("Canot load ad page with non-existent AdImage");
    }
    NetworkUtil.newLink(adImage.getContext(), link);
  }
}
