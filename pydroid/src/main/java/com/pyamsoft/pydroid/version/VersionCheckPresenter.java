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
 *
 */

package com.pyamsoft.pydroid.version;

import android.support.annotation.NonNull;
import com.pyamsoft.pydroid.helper.Checker;
import com.pyamsoft.pydroid.presenter.SchedulerPresenter;
import io.reactivex.Scheduler;
import retrofit2.HttpException;
import timber.log.Timber;

public class VersionCheckPresenter extends SchedulerPresenter {

  @NonNull private final VersionCheckInteractor interactor;

  @SuppressWarnings("WeakerAccess")
  public VersionCheckPresenter(@NonNull VersionCheckInteractor interactor,
      @NonNull Scheduler observeScheduler, @NonNull Scheduler subscribeScheduler) {
    super(observeScheduler, subscribeScheduler);
    this.interactor = Checker.checkNonNull(interactor);
  }

  public void forceCheckForUpdates(@NonNull String packageName, int currentVersionCode,
      @NonNull UpdateCheckCallback callback) {
    checkForUpdates(packageName, currentVersionCode, true, callback);
  }

  public void checkForUpdates(@NonNull String packageName, int currentVersionCode,
      @NonNull UpdateCheckCallback callback) {
    checkForUpdates(packageName, currentVersionCode, false, callback);
  }

  private void checkForUpdates(@NonNull String packageName, int currentVersionCode, boolean force,
      @NonNull UpdateCheckCallback callback) {
    disposeOnStop(interactor.checkVersion(packageName, force)
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(responseVersionCode -> {
          Timber.i("Update check finished");
          Timber.i("Current version: %d", currentVersionCode);
          Timber.i("Latest version: %d", responseVersionCode);
          callback.onVersionCheckFinished();
          if (currentVersionCode < responseVersionCode) {
            callback.onUpdatedVersionFound(currentVersionCode, responseVersionCode);
          }
        }, throwable -> {
          if (throwable instanceof HttpException) {
            Timber.e(throwable, "Network Failure: %d", ((HttpException) throwable).code());
          } else {
            Timber.e(throwable, "onError");
          }
        }));
  }

  public interface UpdateCheckCallback {

    void onVersionCheckFinished();

    void onUpdatedVersionFound(int oldVersionCode, int updatedVersionCode);
  }
}
