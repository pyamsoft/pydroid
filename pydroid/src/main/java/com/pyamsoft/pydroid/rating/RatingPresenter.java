/*
 * Copyright 2017 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.rating;

import android.support.annotation.NonNull;
import com.pyamsoft.pydroid.presenter.SchedulerPresenter;
import io.reactivex.Scheduler;
import timber.log.Timber;

public class RatingPresenter extends SchedulerPresenter {

  @NonNull private final RatingInteractor interactor;

  @SuppressWarnings("WeakerAccess")
  public RatingPresenter(@NonNull RatingInteractor interactor, @NonNull Scheduler observeScheduler,
      @NonNull Scheduler subscribeScheduler) {
    super(observeScheduler, subscribeScheduler);
    this.interactor = interactor;
  }

  public void loadRatingDialog(int currentVersion, boolean force,
      @NonNull RatingCallback callback) {
    disposeOnDestroy(interactor.needsToViewRating(currentVersion, force)
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .doAfterTerminate(callback::onLoadComplete)
        .subscribe(show -> {
          if (show) {
            callback.onShowRatingDialog();
          }
        }, throwable -> {
          Timber.e(throwable, "on error loading rating dialog");
          callback.onRatingDialogLoadError(throwable);
        }));
  }

  public void saveRating(int versionCode, @NonNull SaveCallback callback) {
    disposeOnDestroy(interactor.saveRating(versionCode)
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(() -> {
          Timber.d("Saved current version code: %d", versionCode);
          callback.onRatingSaved();
        }, throwable -> {
          Timber.e(throwable, "on error loading rating dialog");
          callback.onRatingDialogSaveError(throwable);
        }));
  }

  public interface RatingCallback {

    void onShowRatingDialog();

    void onRatingDialogLoadError(@NonNull Throwable throwable);

    void onLoadComplete();
  }

  public interface SaveCallback {

    void onRatingSaved();

    void onRatingDialogSaveError(@NonNull Throwable throwable);
  }
}
