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
import android.support.annotation.Nullable;
import com.pyamsoft.pydroid.presenter.PresenterBase;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

class VersionCheckPresenterImpl extends PresenterBase<VersionCheckPresenter.View>
    implements VersionCheckPresenter {

  @NonNull private final VersionCheckInteractor interactor;
  @Nullable private Call<VersionCheckResponse> call;

  VersionCheckPresenterImpl(@NonNull VersionCheckInteractor interactor) {
    this.interactor = interactor;
  }

  @Override protected void onUnbind() {
    super.onUnbind();
    cancelCall();
  }

  @Override public void checkForUpdates(int currentVersionCode) {
    cancelCall();
    call = interactor.checkVersion();
    call.enqueue(new Callback<VersionCheckResponse>() {
      @Override public void onResponse(Call<VersionCheckResponse> call,
          Response<VersionCheckResponse> response) {
        if (response.isSuccessful()) {
          final VersionCheckResponse versionCheckResponse = response.body();
          Timber.i("Update check finished");
          Timber.i("Current version: %d", currentVersionCode);
          Timber.i("Latest version: %d", versionCheckResponse.currentVersion());
          getView(View::onVersionCheckFinished);
          if (currentVersionCode < versionCheckResponse.currentVersion()) {
            getView(view -> view.onUpdatedVersionFound(currentVersionCode,
                versionCheckResponse.currentVersion()));
            cancelCall();
          }
        } else {
          Timber.w("onResponse: Not successful CODE: %d", response.code());
          cancelCall();
        }
      }

      @Override public void onFailure(Call<VersionCheckResponse> call, Throwable t) {
        Timber.e(t, "onError checkForUpdates");
        cancelCall();
      }
    });
  }

  @SuppressWarnings("WeakerAccess") void cancelCall() {
    if (call != null) {
      if (!call.isCanceled()) {
        call.cancel();
      }
    }
  }
}
