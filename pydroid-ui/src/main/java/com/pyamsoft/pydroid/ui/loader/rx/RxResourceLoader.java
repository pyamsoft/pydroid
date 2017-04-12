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

package com.pyamsoft.pydroid.ui.loader.rx;

import android.graphics.drawable.Drawable;
import android.support.annotation.CheckResult;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import com.pyamsoft.pydroid.function.ActionSingle;
import com.pyamsoft.pydroid.helper.Checker;
import com.pyamsoft.pydroid.helper.SchedulerHelper;
import com.pyamsoft.pydroid.ui.loader.Loaded;
import com.pyamsoft.pydroid.ui.loader.ResourceLoader;
import com.pyamsoft.pydroid.ui.loader.targets.Target;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class RxResourceLoader extends ResourceLoader.Loader {

  @NonNull private Scheduler obsScheduler;
  @NonNull private Scheduler subScheduler;

  public RxResourceLoader() {
    obsScheduler = AndroidSchedulers.mainThread();
    subScheduler = Schedulers.io();

    SchedulerHelper.enforceObserveScheduler(obsScheduler);
    SchedulerHelper.enforceSubscribeScheduler(subScheduler);
  }

  @NonNull @Override
  protected Loaded load(@NonNull Target<Drawable> target, @DrawableRes int resource) {
    final Target<Drawable> finalTarget = Checker.checkNonNull(target);
    return new RxLoaded(Observable.fromCallable(finalTarget::getContext)
        .map(this::loadResource)
        .subscribeOn(subScheduler)
        .observeOn(obsScheduler)
        .doOnSubscribe(disposable -> {
          ActionSingle<Target<Drawable>> startAction = startAction();
          if (startAction != null) {
            startAction.call(finalTarget);
          }
        })
        .doOnError(throwable -> {
          Timber.e(throwable, "Error loading AsyncDrawable");
          ActionSingle<Target<Drawable>> errorAction = errorAction();
          if (errorAction != null) {
            errorAction.call(finalTarget);
          }
        })
        .doOnComplete(() -> {
          ActionSingle<Target<Drawable>> completeAction = completeAction();
          if (completeAction != null) {
            completeAction.call(finalTarget);
          }
        })
        .subscribe(finalTarget::loadImage,
            throwable -> Timber.e(throwable, "Error loading drawable")));
  }

  @CheckResult @NonNull public final RxResourceLoader subscribeOn(@NonNull Scheduler scheduler) {
    this.subScheduler = Checker.checkNonNull(scheduler);
    SchedulerHelper.enforceSubscribeScheduler(subScheduler);
    return this;
  }

  @CheckResult @NonNull public final RxResourceLoader observeOn(@NonNull Scheduler scheduler) {
    this.obsScheduler = Checker.checkNonNull(scheduler);
    SchedulerHelper.enforceObserveScheduler(obsScheduler);
    return this;
  }
}
