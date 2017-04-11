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

package com.pyamsoft.pydroid.rating;

import android.support.annotation.NonNull;
import com.pyamsoft.pydroid.helper.DisposableHelper;
import com.pyamsoft.pydroid.presenter.SchedulerPresenter;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import timber.log.Timber;

public class RatingPresenter extends SchedulerPresenter {

  @NonNull private final RatingInteractor interactor;
  @NonNull private Disposable loadDisposable = Disposables.empty();
  @NonNull private Disposable saveDisposable = Disposables.empty();

  public RatingPresenter(@NonNull RatingInteractor interactor, @NonNull Scheduler observeScheduler,
      @NonNull Scheduler subscribeScheduler) {
    super(observeScheduler, subscribeScheduler);
    this.interactor = interactor;
  }

  @Override protected void onDestroy() {
    loadDisposable = DisposableHelper.dispose(loadDisposable);
    saveDisposable = DisposableHelper.dispose(saveDisposable);
  }

  public void loadRatingDialog(int currentVersion, boolean force,
      @NonNull RatingCallback callback) {
    loadDisposable = DisposableHelper.dispose(loadDisposable);
    loadDisposable = interactor.needsToViewRating(currentVersion, force)
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
        });
  }

  public void saveRating(int versionCode, @NonNull SaveCallback callback) {
    saveDisposable = DisposableHelper.dispose(saveDisposable);
    saveDisposable = interactor.saveRating(versionCode)
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(() -> {
          Timber.d("Saved current version code: %d", versionCode);
          callback.onRatingSaved();
        }, throwable -> {
          Timber.e(throwable, "on error loading rating dialog");
          callback.onRatingDialogSaveError(throwable);
        });
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
