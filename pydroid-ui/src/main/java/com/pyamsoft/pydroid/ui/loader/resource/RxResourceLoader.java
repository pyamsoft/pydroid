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

package com.pyamsoft.pydroid.ui.loader.resource;

import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import com.pyamsoft.pydroid.helper.Checker;
import com.pyamsoft.pydroid.ui.loader.loaded.Loaded;
import com.pyamsoft.pydroid.ui.loader.loaded.RxLoaded;
import com.pyamsoft.pydroid.ui.loader.targets.Target;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class RxResourceLoader extends ResourceLoader {

  @NonNull private final Scheduler obsScheduler;
  @NonNull private final Scheduler subScheduler;

  public RxResourceLoader(@DrawableRes int resource) {
    super(resource);
    obsScheduler = AndroidSchedulers.mainThread();
    subScheduler = Schedulers.io();
  }

  @NonNull @Override
  protected Loaded load(@NonNull Target<Drawable> target, @DrawableRes int resource) {
    final Target<Drawable> finalTarget = Checker.checkNonNull(target);
    return new RxLoaded(Single.fromCallable(() -> loadResource(appContext))
        .subscribeOn(subScheduler)
        .observeOn(obsScheduler)
        .doOnSubscribe(disposable -> {
          if (startAction != null) {
            startAction.call(finalTarget);
          }
        })
        .doOnError(throwable -> {
          Timber.e(throwable, "Error loading AsyncDrawable");
          if (errorAction != null) {
            errorAction.call(finalTarget);
          }
        })
        .doAfterSuccess(drawable -> {
          if (completeAction != null) {
            completeAction.call(finalTarget);
          }
        })
        .subscribe(finalTarget::loadImage,
            throwable -> Timber.e(throwable, "Error loading Drawable using RxResourceLoader")));
  }
}
