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
import com.pyamsoft.pydroid.presenter.Presenter;
import timber.log.Timber;

@RestrictTo(RestrictTo.Scope.LIBRARY) public class AdvertisementPresenter
    extends Presenter<Presenter.Empty> {

  @NonNull private final AdvertisementInteractor interactor;
  @SuppressWarnings("WeakerAccess") @NonNull ExecutedOffloader offloader =
      new ExecutedOffloader.Empty();

  AdvertisementPresenter(@NonNull AdvertisementInteractor interactor) {
    this.interactor = interactor;
  }

  @Override protected void onUnbind() {
    super.onUnbind();
    OffloaderHelper.cancel(offloader);
  }

  public void showAd(@NonNull ShowAdCallback callback) {
    OffloaderHelper.cancel(offloader);
    offloader = interactor.showAdView()
        .onError(item -> Timber.e(item, "onError showAd"))
        .onResult(shown -> {
          if (shown) {
            callback.onShown();
          }
        })
        .onFinish(() -> OffloaderHelper.cancel(offloader))
        .execute();
  }

  public void hideAd(@NonNull HideAdCallback callback) {
    OffloaderHelper.cancel(offloader);
    offloader =
        interactor.hideAdView().onError(item -> Timber.e(item, "onError hideAd")).onResult(hide -> {
          if (hide) {
            callback.onHidden();
          }
        }).onFinish(() -> OffloaderHelper.cancel(offloader)).execute();
  }

  public interface ShowAdCallback {

    void onShown();
  }

  public interface HideAdCallback {

    void onHidden();
  }
}
