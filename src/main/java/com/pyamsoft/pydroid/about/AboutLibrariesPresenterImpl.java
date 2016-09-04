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

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import com.pyamsoft.pydroid.app.presenter.SchedulerPresenter;
import rx.Scheduler;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

class AboutLibrariesPresenterImpl<VH extends RecyclerView.ViewHolder>
    extends SchedulerPresenter<AboutLibrariesPresenter.View<VH>>
    implements AboutLibrariesPresenter<VH> {

  @NonNull private final AboutLibrariesInteractor interactor;
  @NonNull private Subscription licenseSubscription = Subscriptions.empty();

  protected AboutLibrariesPresenterImpl(@NonNull Context context) {
    this(context, Schedulers.io(), AndroidSchedulers.mainThread());
  }

  protected AboutLibrariesPresenterImpl(@NonNull Context context,
      @NonNull Scheduler observeScheduler, @NonNull Scheduler subscribeScheduler) {
    super(observeScheduler, subscribeScheduler);
    interactor = new AboutLibrariesInteractorImpl(context.getApplicationContext());
  }

  @Override protected void onUnbind() {
    super.onUnbind();
    unsubLoadLicense();
  }

  @Override public void loadLicenseText(@NonNull VH holder, @NonNull Licenses licenses) {
    unsubLoadLicense();
    licenseSubscription = interactor.loadLicenseText(licenses)
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(license -> getView().onLicenseTextLoaded(holder, license), throwable -> {
          Timber.e(throwable, "Failed to load license");
          getView().onLicenseTextLoaded(holder, "Failed to load license");
        }, this::unsubLoadLicense);
  }

  void unsubLoadLicense() {
    if (!licenseSubscription.isUnsubscribed()) {
      licenseSubscription.unsubscribe();
    }
  }
}
