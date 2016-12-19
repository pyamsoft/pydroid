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
import com.pyamsoft.pydroid.presenter.PresenterBase;
import com.pyamsoft.pydroid.tool.ExecutedOffloader;
import com.pyamsoft.pydroid.tool.OffloaderHelper;
import timber.log.Timber;

class AdvertisementPresenterImpl extends PresenterBase<AdvertisementPresenter.AdView>
    implements AdvertisementPresenter {

  @NonNull private final AdvertisementInteractor interactor;
  @SuppressWarnings("WeakerAccess") @NonNull ExecutedOffloader offloader =
      new ExecutedOffloader.Empty();

  AdvertisementPresenterImpl(@NonNull AdvertisementInteractor interactor) {
    this.interactor = interactor;
  }

  @Override protected void onBind() {
    super.onBind();
    showAd();
  }

  @Override protected void onUnbind() {
    super.onUnbind();
    OffloaderHelper.cancel(offloader);
  }

  @Override protected void onDestroy() {
    super.onDestroy();
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
        .onFinish(() -> OffloaderHelper.cancel(offloader))
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
        .onFinish(() -> OffloaderHelper.cancel(offloader))
        .execute();
  }
}
