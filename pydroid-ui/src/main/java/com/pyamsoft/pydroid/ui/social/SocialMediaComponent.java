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

package com.pyamsoft.pydroid.ui.social;

import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import com.pyamsoft.pydroid.helper.Checker;
import com.pyamsoft.pydroid.social.SocialMediaModule;
import com.pyamsoft.pydroid.ui.ads.OfflineAdSource;
import com.pyamsoft.pydroid.ui.sec.TamperDialog;
import com.pyamsoft.pydroid.ui.version.VersionUpgradeDialog;

@RestrictTo(RestrictTo.Scope.LIBRARY) public class SocialMediaComponent {

  @NonNull private final SocialMediaModule socialMediaModule;

  public SocialMediaComponent(@NonNull SocialMediaModule socialMediaModule) {
    this.socialMediaModule = Checker.checkNonNull(socialMediaModule);
  }

  public void inject(@NonNull TamperDialog dialog) {
    Checker.checkNonNull(dialog).presenter = socialMediaModule.getPresenter();
  }

  public void inject(@NonNull VersionUpgradeDialog dialog) {
    Checker.checkNonNull(dialog).presenter = socialMediaModule.getPresenter();
  }

  public void inject(@NonNull SocialMediaPreference preference) {
    Checker.checkNonNull(preference).presenter = socialMediaModule.getPresenter();
  }

  public void inject(@NonNull OfflineAdSource offlineAdSource) {
    Checker.checkNonNull(offlineAdSource).presenter = socialMediaModule.getPresenter();
  }
}
