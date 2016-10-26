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

package com.pyamsoft.pydroid.ads;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.pyamsoft.pydroid.PYDroidModule;
import com.pyamsoft.pydroid.social.SocialMediaModule;

public class AdvertisementModule {

  @NonNull private final AdvertisementInteractor interactor;
  @NonNull private final AdvertisementPresenter presenter;
  @NonNull private final AdvertisementPresenterLoader loader;

  public AdvertisementModule(@NonNull PYDroidModule.Provider pyDroidModule,
      @NonNull SocialMediaModule socialMediaModule) {
    interactor = new AdvertisementInteractorImpl(pyDroidModule.provideContext());
    presenter = new AdvertisementPresenterImpl(interactor, socialMediaModule.getPresenter());
    loader = new AdvertisementPresenterLoader(presenter);
  }

  @NonNull @CheckResult public final AdvertisementPresenterLoader getLoader() {
    return loader;
  }
}
