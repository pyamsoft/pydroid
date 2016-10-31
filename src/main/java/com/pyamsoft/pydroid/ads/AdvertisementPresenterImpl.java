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
import com.pyamsoft.pydroid.tool.OffloaderHelper;
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

  @Override protected void onUnbind() {
    super.onUnbind();
    socialMediaPresenter.unbindView();
    OffloaderHelper.cancel(offloader);
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    socialMediaPresenter.destroy();

    hideAd();
  }

  @Override public void showAd() {
    OffloaderHelper.cancel(offloader);
    offloader = interactor.showAdView()
        .onError(item -> Timber.e(item, "onError showAd"))
        .onResult(shown -> getView(view -> {
          if (shown) {
            view.onShown();
          }
        }))
        .execute();
  }

  @Override public void hideAd() {
    OffloaderHelper.cancel(offloader);
    offloader = interactor.hideAdView()
        .onError(item -> Timber.e(item, "onError hideAd"))
        .onResult(hide -> getView(view -> {
          if (hide) {
            view.onHidden();
          }
        }))
        .execute();
  }

  @Override public void clickAd(@NonNull String packageName) {
    socialMediaPresenter.clickAppPage(packageName);
  }
}
