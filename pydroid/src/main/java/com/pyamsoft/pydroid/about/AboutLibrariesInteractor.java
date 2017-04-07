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

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import io.reactivex.Observable;
import java.util.Collections;
import java.util.List;

public class AboutLibrariesInteractor {

  @SuppressWarnings("WeakerAccess") @NonNull final List<AboutLibrariesModel> licenses;

  public AboutLibrariesInteractor(@NonNull List<AboutLibrariesModel> licenses) {
    this.licenses = Collections.unmodifiableList(licenses);
  }

  /**
   * public
   */
  @NonNull @CheckResult Observable<AboutLibrariesModel> loadLicenses(boolean hasGooglePlay) {
    return Observable.defer(() -> Observable.fromIterable(licenses))
        .filter(model -> !Licenses.Names.GOOGLE_PLAY.equals(model.name()) || hasGooglePlay)
        .toSortedList((o1, o2) -> o1.name().compareTo(o2.name()))
        .toObservable()
        .concatMap(Observable::fromIterable);
  }
}
