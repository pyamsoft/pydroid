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

package com.pyamsoft.pydroid.support;

import android.support.annotation.NonNull;
import com.pyamsoft.pydroid.app.presenter.PresenterBase;

class SocialMediaPresenterImpl extends PresenterBase<SocialMediaPresenter.View>
    implements SocialMediaPresenter {

  @NonNull private static final String BASE_MARKET = "market://details?id=";
  @NonNull private static final String FACEBOOK = "https://www.facebook.com/pyamsoftware";
  @NonNull private static final String GOOGLE_PLAY_DEVELOPER_PAGE =
      "https://play.google.com/store/apps/dev?id=5257476342110165153";
  @NonNull private static final String GOOGLE_PLUS =
      "https://plus.google.com/+Pyamsoft-officialBlogspot/posts";
  @NonNull private static final String OFFICIAL_BLOG = "https://pyamsoft.blogspot.com/";

  SocialMediaPresenterImpl() {
  }

  @Override public void clickAppPage(@NonNull String link) {
    final String fullLink = BASE_MARKET + link;
    getView().onSocialMediaClicked(fullLink);
  }

  @Override public void clickGooglePlay() {
    getView().onSocialMediaClicked(GOOGLE_PLAY_DEVELOPER_PAGE);
  }

  @Override public void clickGooglePlus() {
    getView().onSocialMediaClicked(GOOGLE_PLUS);
  }

  @Override public void clickBlogger() {
    getView().onSocialMediaClicked(OFFICIAL_BLOG);
  }

  @Override public void clickFacebook() {
    getView().onSocialMediaClicked(FACEBOOK);
  }
}
