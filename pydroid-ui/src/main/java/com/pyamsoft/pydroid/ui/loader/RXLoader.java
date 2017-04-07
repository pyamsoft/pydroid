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

package com.pyamsoft.pydroid.ui.loader;

import android.graphics.drawable.Drawable;
import android.support.annotation.CheckResult;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.content.res.AppCompatResources;
import android.widget.ImageView;
import com.pyamsoft.pydroid.function.ActionSingle;
import com.pyamsoft.pydroid.helper.Checker;
import com.pyamsoft.pydroid.util.DrawableUtil;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class RXLoader extends DrawableLoader.Loader {

  @NonNull private Scheduler subscribeScheduler;
  @NonNull private Scheduler observeScheduler;

  public RXLoader() {
    super();
    subscribeScheduler = Schedulers.io();
    observeScheduler = AndroidSchedulers.mainThread();
  }

  @NonNull @Override
  protected DrawableLoader.Loaded load(@NonNull ImageView image, @DrawableRes int resource,
      @ColorRes int tint, @Nullable ActionSingle<ImageView> startAction,
      @Nullable ActionSingle<ImageView> errorAction,
      @Nullable ActionSingle<ImageView> completeAction) {
    ImageView imageView = Checker.checkNonNull(image);
    if (resource == 0) {
      throw new RuntimeException("Drawable resource cannot be 0");
    }
    return new RxLoaded(Observable.fromCallable(imageView::getContext)
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
        .doOnSubscribe(disposable -> {
          if (startAction != null) {
            startAction.call(imageView);
          }
        })
        .doOnError(throwable -> {
          Timber.e(throwable, "Error loading AsyncDrawable");
          if (errorAction != null) {
            errorAction.call(imageView);
          }
        })
        .doOnComplete(() -> {
          if (completeAction != null) {
            completeAction.call(imageView);
          }
        })
        .subscribe(imageView::setImageDrawable,
            throwable -> Timber.e(throwable, "Error loading drawable")));
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

  private static class RxLoaded implements DrawableLoader.Loaded {

    @NonNull private final Disposable disposable;

    RxLoaded(@NonNull Disposable disposable) {
      disposable = Checker.checkNonNull(disposable);
      this.disposable = disposable;
    }

    @Override public void unload() {
      if (!isUnloaded()) {
        disposable.dispose();
      }
    }

    @Override public boolean isUnloaded() {
      return disposable.isDisposed();
    }
  }
}
