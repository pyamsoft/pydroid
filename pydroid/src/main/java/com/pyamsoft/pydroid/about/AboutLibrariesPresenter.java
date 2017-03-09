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
import android.support.annotation.RestrictTo;
import com.pyamsoft.pydroid.presenter.Presenter;
import com.pyamsoft.pydroid.presenter.SchedulerPresenter;
import io.reactivex.Scheduler;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import timber.log.Timber;

@RestrictTo(RestrictTo.Scope.LIBRARY) public class AboutLibrariesPresenter
    extends SchedulerPresenter<Presenter.Empty> {

  @NonNull private final AboutLibrariesInteractor interactor;
  @NonNull private final CompositeDisposable licenseDisposables;

  AboutLibrariesPresenter(@NonNull AboutLibrariesInteractor interactor,
      @NonNull Scheduler observeScheduler, @NonNull Scheduler subscribeScheduler) {
    super(observeScheduler, subscribeScheduler);
    this.interactor = interactor;
    licenseDisposables = new CompositeDisposable();
  }

  @Override protected void onUnbind() {
    super.onUnbind();
    licenseDisposables.clear();
    interactor.clearCache();
  }

  public void loadLicenseText(int position, @NonNull AboutLicenseModel license,
      @NonNull LicenseTextLoadCallback callback) {
    Disposable licenseSubscription = interactor.loadLicenseText(license)
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(licenseText -> {
          callback.onLicenseTextLoadComplete(position, licenseText);
        }, throwable -> {
          Timber.e(throwable, "onError loadLicenseText");
          callback.onLicenseTextLoadError(position);
        });
    licenseDisposables.add(licenseSubscription);
  }

  public interface LicenseTextLoadCallback {

    void onLicenseTextLoadComplete(int position, @NonNull String text);

    void onLicenseTextLoadError(int position);
  }
}
