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

package com.pyamsoft.pydroid.version;

import android.support.annotation.NonNull;
import com.pyamsoft.pydroid.presenter.SchedulerPresenter;
import javax.inject.Inject;
import rx.Scheduler;
import rx.Subscription;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

class VersionCheckPresenterImpl extends SchedulerPresenter<VersionCheckPresenter.View>
    implements VersionCheckPresenter {

  @NonNull private final VersionCheckInteractor interactor;
  @NonNull private Subscription checkSubscription = Subscriptions.empty();

  @Inject VersionCheckPresenterImpl(@NonNull VersionCheckInteractor interactor,
      @NonNull Scheduler observeScheduler, @NonNull Scheduler subscribeScheduler) {
    super(observeScheduler, subscribeScheduler);
    this.interactor = interactor;
  }

  @Override protected void onUnbind() {
    super.onUnbind();
    unsubCheck();
  }

  @Override public void checkForUpdates(int currentVersionCode) {
    unsubCheck();
    checkSubscription = interactor.checkVersion()
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(versionCheckResponse -> {
          Timber.i("Update check finished");
          Timber.i("Current version: %d", currentVersionCode);
          Timber.i("Latest version: %d", versionCheckResponse.currentVersion());
          getView(View::onVersionCheckFinished);
          if (currentVersionCode < versionCheckResponse.currentVersion()) {
            getView(view -> view.onUpdatedVersionFound(currentVersionCode,
                versionCheckResponse.currentVersion()));
          }
        }, throwable -> Timber.e(throwable, "onError checkForUpdates"), this::unsubCheck);
  }

  @SuppressWarnings("WeakerAccess") void unsubCheck() {
    if (!checkSubscription.isUnsubscribed()) {
      checkSubscription.unsubscribe();
    }
  }
}
