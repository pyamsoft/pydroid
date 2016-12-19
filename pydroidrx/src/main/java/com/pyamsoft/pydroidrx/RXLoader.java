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

package com.pyamsoft.pydroidrx;

import android.graphics.drawable.Drawable;
import android.support.annotation.CheckResult;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v7.content.res.AppCompatResources;
import android.widget.ImageView;
import com.pyamsoft.pydroid.tool.AsyncDrawable;
import com.pyamsoft.pydroid.util.DrawableUtil;
import rx.Observable;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

@SuppressWarnings("WeakerAccess") public class RXLoader
    extends AsyncDrawable.Loader<AsyncDrawableSubscriptionEntry> {

  @NonNull Scheduler subscribeScheduler;
  @NonNull Scheduler observeScheduler;

  public RXLoader() {
    super();
    subscribeScheduler = Schedulers.io();
    observeScheduler = AndroidSchedulers.mainThread();
  }

  @NonNull @Override protected AsyncDrawableSubscriptionEntry load(@NonNull ImageView imageView,
      @DrawableRes int resource, @ColorRes int tint) {
    //noinspection ConstantConditions
    if (imageView == null) {
      throw new NullPointerException("ImageView cannot be NULL");
    }

    if (resource == 0) {
      throw new RuntimeException("Drawable resource cannot be 0");
    }
    return new AsyncDrawableSubscriptionEntry(
        Observable.defer(() -> Observable.just(imageView.getContext()))
            .map(context -> {
              Drawable loaded = AppCompatResources.getDrawable(context, resource);
              if (loaded == null) {
                throw new NullPointerException("Could not load drawable for resource: " + resource);
              }

              if (tint != 0) {
                loaded = DrawableUtil.tintDrawableFromRes(context, loaded, tint);
              }
              return loaded;
            })
            .subscribeOn(subscribeScheduler)
            .observeOn(observeScheduler)
            .subscribe(imageView::setImageDrawable, throwable -> {
              Timber.e(throwable, "Error loading Drawable into ImageView");
            }));
  }

  @SuppressWarnings("unused") @CheckResult @NonNull
  public final RXLoader subscribeOn(@NonNull Scheduler scheduler) {
    this.subscribeScheduler = scheduler;
    return this;
  }

  @SuppressWarnings("unused") @CheckResult @NonNull
  public final RXLoader observeOn(@NonNull Scheduler scheduler) {
    this.observeScheduler = scheduler;
    return this;
  }
}
