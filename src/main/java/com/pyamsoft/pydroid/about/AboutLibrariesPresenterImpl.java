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

package com.pyamsoft.pydroid.about;

import android.support.annotation.NonNull;
import com.pyamsoft.pydroid.presenter.SchedulerPresenter;
import javax.inject.Inject;
import rx.Scheduler;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

class AboutLibrariesPresenterImpl extends SchedulerPresenter<AboutLibrariesPresenter.View>
    implements AboutLibrariesPresenter {

  @NonNull private final AboutLibrariesInteractor interactor;
  @NonNull private final CompositeSubscription licenseSubscriptions = new CompositeSubscription();
  @NonNull private Subscription loadLicenseBus = Subscriptions.empty();

  @Inject AboutLibrariesPresenterImpl(@NonNull AboutLibrariesInteractor interactor,
      @NonNull Scheduler observeScheduler, @NonNull Scheduler subscribeScheduler) {
    super(observeScheduler, subscribeScheduler);
    this.interactor = interactor;
  }

  @Override protected void onBind() {
    super.onBind();
    registerOnLicenseBus();
  }

  private void registerOnLicenseBus() {
    unregisterLicenseBus();
    loadLicenseBus = AboutItemBus.get()
        .register()
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(licenseLoadEvent -> {
          loadLicenseText(licenseLoadEvent.position(), licenseLoadEvent.license());
        }, throwable -> {
          Timber.e(throwable, "onError registerOnLicenseBus");
        });
  }

  private void unregisterLicenseBus() {
    if (!loadLicenseBus.isUnsubscribed()) {
      loadLicenseBus.unsubscribe();
    }
  }

  @Override protected void onUnbind() {
    super.onUnbind();
    unregisterLicenseBus();
    unsubLoadLicense();
    interactor.clearCache();
  }

  @SuppressWarnings("WeakerAccess") void loadLicenseText(int position,
      @NonNull AboutLicenseItem license) {
    final Subscription licenseSubscription = interactor.loadLicenseText(license)
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(licenseText -> getView(view -> view.onLicenseTextLoaded(position, licenseText)),
            throwable -> {
              Timber.e(throwable, "Failed to load license");
              getView(view -> view.onLicenseTextLoaded(position, "Failed to load license"));
            }, this::unsubLoadLicense);

    Timber.d("Add license subscription");
    licenseSubscriptions.add(licenseSubscription);
  }

  @SuppressWarnings("WeakerAccess") void unsubLoadLicense() {
    if (licenseSubscriptions.hasSubscriptions()) {
      licenseSubscriptions.clear();
    }
  }
}
