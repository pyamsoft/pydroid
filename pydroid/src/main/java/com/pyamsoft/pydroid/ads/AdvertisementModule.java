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

package com.pyamsoft.pydroid.ads;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import com.pyamsoft.pydroid.PYDroidModule;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class AdvertisementModule {

  @NonNull private final AdvertisementInteractor interactor;

  public AdvertisementModule(@NonNull PYDroidModule pyDroidModule) {
    interactor = new AdvertisementInteractor(pyDroidModule.providePreferences());
  }

  @NonNull @CheckResult public AdvertisementPresenter getPresenter() {
    return new AdvertisementPresenter(interactor);
  }
}
