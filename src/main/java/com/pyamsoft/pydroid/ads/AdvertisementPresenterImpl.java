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
import com.pyamsoft.pydroid.presenter.PresenterBase;
import com.pyamsoft.pydroid.social.SocialMediaPresenter;
import com.pyamsoft.pydroid.tool.ExecutedOffloader;
import timber.log.Timber;

class AdvertisementPresenterImpl extends PresenterBase<AdvertisementPresenter.AdView>
    implements AdvertisementPresenter {

  @SuppressWarnings("WeakerAccess") @NonNull final SocialMediaPresenter socialMediaPresenter;
  @NonNull private final AdvertisementInteractor interactor;
  @NonNull private ExecutedOffloader offloader = new ExecutedOffloader.Empty();

  AdvertisementPresenterImpl(@NonNull AdvertisementInteractor interactor,
      @NonNull SocialMediaPresenter socialMediaPresenter) {
    this.socialMediaPresenter = socialMediaPresenter;
    this.interactor = interactor;
  }

  @Override protected void onBind() {
    super.onBind();
    getView(socialMediaPresenter::bindView);
    showAd();
  }

  @SuppressWarnings("WeakerAccess") void unsubAdSubscription() {
    if (!offloader.isCancelled()) {
      offloader.cancel();
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
    offloader = interactor.showAdView()
        .onError(item -> Timber.e(item, "onError showAd"))
        .onResult(item -> Timber.d("Ad shown: %s", item))
        .execute();
  }

  @Override public void hideAd() {
    unsubAdSubscription();
    offloader = interactor.hideAdView()
        .onError(item -> Timber.e(item, "onError hideAd"))
        .onResult(item -> Timber.d("Ad shown: %s", item))
        .execute();
  }

  @Override public void clickAd(@NonNull String packageName) {
    socialMediaPresenter.clickAppPage(packageName);
  }
}
