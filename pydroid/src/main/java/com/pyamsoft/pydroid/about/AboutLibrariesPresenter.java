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
import java.util.HashSet;
import java.util.Set;
import timber.log.Timber;

@RestrictTo(RestrictTo.Scope.LIBRARY) public class AboutLibrariesPresenter
    extends Presenter<Presenter.Empty> {

  @NonNull private final AboutLibrariesInteractor interactor;
  @NonNull private final Set<ExecutedOffloader> licenseSubscriptions;

  AboutLibrariesPresenter(@NonNull AboutLibrariesInteractor interactor) {
    this.interactor = interactor;
    licenseSubscriptions = new HashSet<>();
  }

  @Override protected void onUnbind() {
    super.onUnbind();
    unsubLoadLicense();
    interactor.clearCache();
  }

  @RestrictTo(RestrictTo.Scope.SUBCLASSES) @SuppressWarnings("WeakerAccess")
  void unsubLoadLicense() {
    //noinspection Convert2streamapi
    for (final ExecutedOffloader task : licenseSubscriptions) {
      OffloaderHelper.cancel(task);
    }
  }

  public void loadLicenseText(int position, @NonNull AboutLicenseModel license,
      @NonNull LicenseTextLoadCallback callback) {
    final ExecutedOffloader licenseSubscription =
        interactor.loadLicenseText(license).onError(throwable -> {
          Timber.e(throwable, "onError loadLicenseText");
          callback.onLicenseTextLoadError(position);
        }).onResult(license1 -> callback.onLicenseTextLoadComplete(position, license1)).execute();

    Timber.d("Add license subscription");
    licenseSubscriptions.add(licenseSubscription);
  }

  public interface LicenseTextLoadCallback {

    void onLicenseTextLoadComplete(int position, @NonNull String text);

    void onLicenseTextLoadError(int position);
  }
}
