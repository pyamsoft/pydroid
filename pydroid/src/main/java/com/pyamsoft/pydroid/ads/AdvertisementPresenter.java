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

package com.pyamsoft.pydroid.ads;

import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import com.pyamsoft.pydroid.helper.SubscriptionHelper;
import com.pyamsoft.pydroid.presenter.Presenter;
import com.pyamsoft.pydroid.presenter.SchedulerPresenter;
import rx.Scheduler;
import rx.Subscription;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

@RestrictTo(RestrictTo.Scope.LIBRARY) public class AdvertisementPresenter
    extends SchedulerPresenter<Presenter.Empty> {

  @NonNull private final AdvertisementInteractor interactor;
  @NonNull private Subscription adSubscription = Subscriptions.empty();

  AdvertisementPresenter(@NonNull AdvertisementInteractor interactor,
      @NonNull Scheduler observeScheduler, @NonNull Scheduler subscribeScheduler) {
    super(observeScheduler, subscribeScheduler);
    this.interactor = interactor;
  }

  @Override protected void onUnbind() {
    super.onUnbind();
    adSubscription = SubscriptionHelper.unsubscribe(adSubscription);
  }

  public void showAd(@NonNull ShowAdCallback callback) {
    adSubscription = SubscriptionHelper.unsubscribe(adSubscription);
    adSubscription = interactor.showAdView()
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(show -> {
          if (show) {
            callback.onShown();
          }
        }, throwable -> Timber.e(throwable, "onError showAd"));
  }

  public void hideAd(@NonNull HideAdCallback callback) {
    adSubscription = SubscriptionHelper.unsubscribe(adSubscription);
    adSubscription = interactor.hideAdView()
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(hide -> {
          if (hide) {
            callback.onHidden();
          }
        }, throwable -> Timber.e(throwable, "onError hideAd"));
  }

  public interface ShowAdCallback {

    void onShown();
  }

  public interface HideAdCallback {

    void onHidden();
  }
}
