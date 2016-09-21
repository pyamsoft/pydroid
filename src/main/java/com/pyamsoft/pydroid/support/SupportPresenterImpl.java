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

package com.pyamsoft.pydroid.support;

import android.support.annotation.NonNull;
import com.pyamsoft.pydroid.presenter.SchedulerPresenter;
import javax.inject.Inject;
import rx.Scheduler;
import rx.Subscription;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

class SupportPresenterImpl extends SchedulerPresenter<SupportPresenter.View>
    implements SupportPresenter {

  @NonNull private Subscription busSubscription = Subscriptions.empty();

  @Inject SupportPresenterImpl(@NonNull Scheduler observeScheduler,
      @NonNull Scheduler subscribeScheduler) {
    super(observeScheduler, subscribeScheduler);
  }

  @Override protected void onBind() {
    super.onBind();
    registerOnDonationResultBus();
  }

  private void registerOnDonationResultBus() {
    unregisterDonationResultBus();
    busSubscription = SupportBus.get()
        .register()
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(donationResult -> {
          getView(view -> view.onDonationResult(donationResult.requestCode(),
              donationResult.resultCode(), donationResult.data()));
        }, throwable -> {
          Timber.e(throwable, "onError registerOnDonationResultBus");
        });
  }

  @Override protected void onUnbind() {
    super.onUnbind();
    unregisterDonationResultBus();
  }

  private void unregisterDonationResultBus() {
    if (!busSubscription.isUnsubscribed()) {
      busSubscription.unsubscribe();
    }
  }
}
