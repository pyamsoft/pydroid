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

package com.pyamsoft.pydroid.social;

import android.support.annotation.NonNull;
import com.pyamsoft.pydroid.base.Presenter;

public final class SocialMediaPresenter extends Presenter<SocialMediaPresenter.SocialMediaView> {

  public final void clickAppPage(@NonNull String link) {
    final SocialMediaView mediaView = getView();
    final String fullLink = "market://details?id=" + link;
    mediaView.onAppPageClicked(fullLink);
  }

  public final void clickGooglePlay() {
    final SocialMediaView mediaView = getView();
    final String link = "https://play.google.com/store/apps/dev?id=5257476342110165153";
    mediaView.onGooglePlayClicked(link);
  }

  public final void clickGooglePlus() {
    final SocialMediaView mediaView = getView();
    final String link = "https://plus.google.com/+Pyamsoft-officialBlogspot/posts";
    mediaView.onGooglePlusClicked(link);
  }

  public final void clickBlogger() {
    final SocialMediaView mediaView = getView();
    final String link = "http://pyamsoft.blogspot.com/";
    mediaView.onBloggerClicked(link);
  }

  public final void clickFacebook() {
    final SocialMediaView mediaView = getView();
    final String link = "https://www.facebook.com/pyamsoftware";
    mediaView.onFacebookClicked(link);
  }

  public interface SocialMediaView {

    void onAppPageClicked(@NonNull String link);

    void onGooglePlayClicked(@NonNull String link);

    void onGooglePlusClicked(@NonNull String link);

    void onBloggerClicked(@NonNull String link);

    void onFacebookClicked(@NonNull String link);
  }
}
