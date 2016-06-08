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
import com.pyamsoft.pydroid.base.PresenterImpl;

public final class SocialMediaPresenterImpl
    extends PresenterImpl<SocialMediaPresenter.SocialMediaView> implements SocialMediaPresenter {

  @Override public void clickAppPage(@NonNull String link) {
    final SocialMediaView mediaView = getView();
    final String fullLink = "market://details?id=" + link;
    mediaView.onAppPageClicked(fullLink);
  }

  @Override public void clickGooglePlay() {
    final SocialMediaView mediaView = getView();
    final String link = "https://play.google.com/store/apps/dev?id=5257476342110165153";
    mediaView.onGooglePlayClicked(link);
  }

  @Override public void clickGooglePlus() {
    final SocialMediaView mediaView = getView();
    final String link = "https://plus.google.com/+Pyamsoft-officialBlogspot/posts";
    mediaView.onGooglePlusClicked(link);
  }

  @Override public void clickBlogger() {
    final SocialMediaView mediaView = getView();
    final String link = "http://pyamsoft.blogspot.com/";
    mediaView.onBloggerClicked(link);
  }

  @Override public void clickFacebook() {
    final SocialMediaView mediaView = getView();
    final String link = "https://www.facebook.com/pyamsoftware";
    mediaView.onFacebookClicked(link);
  }
}
