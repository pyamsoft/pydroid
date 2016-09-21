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

package com.pyamsoft.pydroid.ads;

import android.support.annotation.NonNull;
import com.pyamsoft.pydroid.presenter.SchedulerPresenter;
import com.pyamsoft.pydroid.social.SocialMediaPresenter;
import javax.inject.Inject;
import rx.Scheduler;
import rx.Subscription;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

class AdvertisementPresenterImpl extends SchedulerPresenter<AdvertisementPresenter.AdView>
    implements AdvertisementPresenter {

  @SuppressWarnings("WeakerAccess") @NonNull final SocialMediaPresenter socialMediaPresenter;
  @NonNull private final AdvertisementInteractor interactor;
  @NonNull private Subscription adSubscription = Subscriptions.empty();

  @Inject AdvertisementPresenterImpl(@NonNull AdvertisementInteractor interactor,
      @NonNull SocialMediaPresenter socialMediaPresenter, @NonNull Scheduler observeScheduler,
      @NonNull Scheduler subscribeScheduler) {
    super(observeScheduler, subscribeScheduler);
    this.socialMediaPresenter = socialMediaPresenter;
    this.interactor = interactor;
  }

  @Override protected void onBind() {
    super.onBind();
    getView(socialMediaPresenter::bindView);

    showAd();
  }

  @SuppressWarnings("WeakerAccess") void unsubAdSubscription() {
    if (!adSubscription.isUnsubscribed()) {
      adSubscription.unsubscribe();
    }
  }

  @Override protected void onUnbind() {
    super.onUnbind();
    socialMediaPresenter.unbindView();
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    socialMediaPresenter.destroy();

    hideAd();
  }

  @Override public void showAd() {
    unsubAdSubscription();
    adSubscription = interactor.showAdView()
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(show -> getView(adView -> {
          if (show) {
            adView.onShown();
          }
        }), throwable -> Timber.e(throwable, "onError showAdView"), this::unsubAdSubscription);
  }

  @Override public void hideAd() {
    unsubAdSubscription();
    adSubscription = interactor.hideAdView()
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(hide -> getView(adView -> {
          if (hide) {
            adView.onHidden();
          }
        }), throwable -> Timber.e(throwable, "onError hideAdView"), this::unsubAdSubscription);
  }

  @Override public void clickAd(@NonNull String packageName) {
    socialMediaPresenter.clickAppPage(packageName);
  }
}
