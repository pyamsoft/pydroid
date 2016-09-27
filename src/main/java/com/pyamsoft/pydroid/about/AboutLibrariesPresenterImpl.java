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

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.os.AsyncTaskCompat;
import com.pyamsoft.pydroid.Bus;
import com.pyamsoft.pydroid.presenter.PresenterBase;
import java.util.HashSet;
import java.util.Set;
import timber.log.Timber;

class AboutLibrariesPresenterImpl extends PresenterBase<AboutLibrariesPresenter.View>
    implements AboutLibrariesPresenter {

  @NonNull private final AboutLibrariesInteractor interactor;
  @NonNull private final Set<AsyncTask> licenseSubscriptions;
  @Nullable private Bus.Event<AboutLicenseLoadEvent> loadLicenseBus;

  AboutLibrariesPresenterImpl(@NonNull AboutLibrariesInteractor interactor) {
    this.interactor = interactor;
    licenseSubscriptions = new HashSet<>();
  }

  @Override protected void onBind() {
    super.onBind();
    registerOnLicenseBus();
  }

  private void registerOnLicenseBus() {
    unregisterLicenseBus();
    loadLicenseBus = AboutItemBus.get()
        .register(aboutLicenseLoadEvent -> loadLicenseText(aboutLicenseLoadEvent.position(),
            aboutLicenseLoadEvent.license()),
            throwable -> Timber.e(throwable, "onError registerOnLicenseBus"));
  }

  private void unregisterLicenseBus() {
    AboutItemBus.get().unregister(loadLicenseBus);
  }

  @Override protected void onUnbind() {
    super.onUnbind();
    unregisterLicenseBus();
    unsubLoadLicense();
    interactor.clearCache();
  }

  @SuppressWarnings("WeakerAccess") void loadLicenseText(int position,
      @NonNull AboutLicenseItem license) {
    final AsyncTask licenseSubscription = AsyncTaskCompat.executeParallel(
        interactor.loadLicenseText(license,
            item -> getView(view -> view.onLicenseTextLoaded(position, item))));

    Timber.d("Add license subscription");
    licenseSubscriptions.add(licenseSubscription);
  }

  @SuppressWarnings("WeakerAccess") void unsubLoadLicense() {
    for (final AsyncTask task : licenseSubscriptions) {
      if (task != null) {
        if (!task.isCancelled()) {
          task.cancel(true);
        }
      }
    }
  }
}
