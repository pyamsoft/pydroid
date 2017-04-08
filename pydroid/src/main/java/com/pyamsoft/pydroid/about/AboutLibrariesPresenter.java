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

package com.pyamsoft.pydroid.about;

import android.support.annotation.NonNull;
import com.pyamsoft.pydroid.helper.DisposableHelper;
import com.pyamsoft.pydroid.presenter.SchedulerPresenter;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import timber.log.Timber;

public class AboutLibrariesPresenter extends SchedulerPresenter {

  @NonNull private final AboutLibrariesInteractor interactor;
  @NonNull private Disposable licenseDisposable = Disposables.empty();

  @SuppressWarnings("WeakerAccess")
  public AboutLibrariesPresenter(@NonNull AboutLibrariesInteractor interactor,
      @NonNull Scheduler observeScheduler, @NonNull Scheduler subscribeScheduler) {
    super(observeScheduler, subscribeScheduler);
    this.interactor = interactor;
  }

  @Override protected void onStop() {
    super.onStop();
    licenseDisposable = DisposableHelper.dispose(licenseDisposable);
  }

  public void loadLicenses(@NonNull LoadCallback callback) {
    licenseDisposable = DisposableHelper.dispose(licenseDisposable);
    licenseDisposable = interactor.loadLicenses()
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(callback::onLicenseLoaded,
            throwable -> Timber.e(throwable, "onError loading licenses"));
  }

  public interface LoadCallback {

    void onLicenseLoaded(@NonNull AboutLibrariesModel model);
  }
}
