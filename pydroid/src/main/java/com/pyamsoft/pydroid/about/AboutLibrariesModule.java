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
import android.support.annotation.RestrictTo;
import com.pyamsoft.pydroid.PYDroidModule;
import io.reactivex.Scheduler;

@RestrictTo(RestrictTo.Scope.LIBRARY) public class AboutLibrariesModule {

  @NonNull private final AboutLibrariesItemInteractor itemInteractor;
  @NonNull private final AboutLibrariesInteractor interactor;
  @NonNull private final Scheduler obsScheduler;
  @NonNull private final Scheduler subScheduler;

  // Created once per "scope"
  public AboutLibrariesModule(@NonNull PYDroidModule pyDroidModule) {
    interactor = new AboutLibrariesInteractor(pyDroidModule.provideLicenseMap());
    itemInteractor = new AboutLibrariesItemInteractor(pyDroidModule.provideContext(),
        pyDroidModule.provideLicenseProvider());
    obsScheduler = pyDroidModule.provideObsScheduler();
    subScheduler =pyDroidModule.provideSubScheduler();
  }

  @NonNull @CheckResult public AboutLibrariesItemPresenter getItemPresenter() {
    return new AboutLibrariesItemPresenter(itemInteractor, obsScheduler, subScheduler);
  }

  @NonNull @CheckResult public AboutLibrariesPresenter getPresenter() {
    return new AboutLibrariesPresenter(interactor, obsScheduler, subScheduler);
  }

}
