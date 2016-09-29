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

import android.support.annotation.Nullable;
import com.pyamsoft.pydroid.presenter.PresenterBase;
import com.pyamsoft.pydroid.tool.Bus;
import timber.log.Timber;

class SupportPresenterImpl extends PresenterBase<SupportPresenter.View>
    implements SupportPresenter {

  @Nullable private Bus.Event<DonationResult> busRegistration;

  SupportPresenterImpl() {
  }

  @Override protected void onBind() {
    super.onBind();
    registerOnDonationResultBus();
  }

  private void registerOnDonationResultBus() {
    unregisterDonationResultBus();
    busRegistration = SupportBus.get()
        .register(event -> getView(
            view -> view.onDonationResult(event.requestCode(), event.resultCode(), event.data())),
            throwable -> Timber.e(throwable, "onError registerOnDonationResultBus"));
  }

  @Override protected void onUnbind() {
    super.onUnbind();
    unregisterDonationResultBus();
  }

  private void unregisterDonationResultBus() {
    SupportBus.get().unregister(busRegistration);
  }
}
