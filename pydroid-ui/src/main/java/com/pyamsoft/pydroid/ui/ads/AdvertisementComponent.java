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

package com.pyamsoft.pydroid.ui.ads;

import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import com.pyamsoft.pydroid.ads.AdvertisementModule;

@RestrictTo(RestrictTo.Scope.LIBRARY) public class AdvertisementComponent {

  @NonNull private final AdvertisementModule advertisementModule;

  public AdvertisementComponent(@NonNull AdvertisementModule advertisementModule) {
    this.advertisementModule = advertisementModule;
  }

  void inject(@NonNull AdvertisementView view) {
    view.presenter = advertisementModule.getPresenter();
  }
}
