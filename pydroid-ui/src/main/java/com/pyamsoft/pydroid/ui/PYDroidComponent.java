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

package com.pyamsoft.pydroid.ui;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import com.pyamsoft.pydroid.PYDroidModule;
import com.pyamsoft.pydroid.about.AboutLibrariesModule;
import com.pyamsoft.pydroid.ads.AdvertisementModule;
import com.pyamsoft.pydroid.donate.DonateModule;
import com.pyamsoft.pydroid.social.SocialMediaModule;
import com.pyamsoft.pydroid.ui.about.AboutLibrariesComponent;
import com.pyamsoft.pydroid.ui.ads.AdvertisementComponent;
import com.pyamsoft.pydroid.ui.app.fragment.AppComponent;
import com.pyamsoft.pydroid.ui.donate.DonateComponent;
import com.pyamsoft.pydroid.ui.social.SocialMediaComponent;
import com.pyamsoft.pydroid.ui.version.VersionCheckComponent;
import com.pyamsoft.pydroid.version.VersionCheckModule;

@RestrictTo(RestrictTo.Scope.LIBRARY) public class PYDroidComponent {

  @NonNull private final DonateComponent donateComponent;
  @NonNull private final VersionCheckComponent versionCheckComponent;
  @NonNull private final AdvertisementComponent advertisementComponent;
  @NonNull private final AboutLibrariesComponent aboutLibrariesComponent;
  @NonNull private final SocialMediaComponent socialMediaComponent;
  @NonNull private final AppComponent appComponent;

  private PYDroidComponent(@NonNull PYDroidModule module) {
    DonateModule donateModule = new DonateModule(module);
    VersionCheckModule versionCheckModule = new VersionCheckModule();
    AdvertisementModule advertisementModule = new AdvertisementModule(module);
    AboutLibrariesModule aboutLibrariesModule = new AboutLibrariesModule(module);
    SocialMediaModule socialMediaModule = new SocialMediaModule();
    donateComponent = new DonateComponent(donateModule);
    versionCheckComponent = new VersionCheckComponent(versionCheckModule);
    advertisementComponent = new AdvertisementComponent(advertisementModule);
    aboutLibrariesComponent = new AboutLibrariesComponent(aboutLibrariesModule);
    socialMediaComponent = new SocialMediaComponent(socialMediaModule);
    appComponent = new AppComponent(socialMediaModule, versionCheckModule);
  }

  @CheckResult @NonNull static PYDroidComponent withModule(@NonNull PYDroidModule module) {
    return new PYDroidComponent(module);
  }

  @CheckResult @NonNull public DonateComponent provideDonateComponent() {
    return donateComponent;
  }

  @CheckResult @NonNull public VersionCheckComponent provideVersionCheckComponent() {
    return versionCheckComponent;
  }

  @CheckResult @NonNull public AdvertisementComponent provideAdvertisementComponent() {
    return advertisementComponent;
  }

  @CheckResult @NonNull public AboutLibrariesComponent provideAboutLibrariesComponent() {
    return aboutLibrariesComponent;
  }

  @CheckResult @NonNull public SocialMediaComponent provideSocialMediaComponent() {
    return socialMediaComponent;
  }

  @CheckResult @NonNull public AppComponent provideAppComponent() {
    return appComponent;
  }
}
