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

import android.content.Context;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import io.reactivex.Observable;
import java.util.Collections;
import java.util.List;

public class AboutLibrariesInteractor {

  @NonNull final Context context;
  @NonNull final LicenseProvider licenseProvider;
  @SuppressWarnings("WeakerAccess") @NonNull final List<AboutLibrariesModel> licenses;

  public AboutLibrariesInteractor(@NonNull Context context,
      @NonNull LicenseProvider licenseProvider, @NonNull List<AboutLibrariesModel> licenses) {
    this.licenses = Collections.unmodifiableList(licenses);
    this.licenseProvider = licenseProvider;
    this.context = context.getApplicationContext();
  }

  /**
   * public
   */
  @NonNull @CheckResult Observable<AboutLibrariesModel> loadLicenses() {
    return Observable.defer(() -> Observable.fromIterable(licenses))
        .filter(model -> !Licenses.Names.GOOGLE_PLAY.equals(model.name())
            || licenseProvider.provideGoogleOpenSourceLicenses(context) != null)
        .toSortedList((o1, o2) -> o1.name().compareTo(o2.name()))
        .toObservable()
        .concatMap(Observable::fromIterable);
  }
}
