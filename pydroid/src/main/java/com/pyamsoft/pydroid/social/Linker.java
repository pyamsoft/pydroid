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

package com.pyamsoft.pydroid.social;

import android.content.Context;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import com.pyamsoft.pydroid.helper.Checker;
import com.pyamsoft.pydroid.presenter.Presenter;
import com.pyamsoft.pydroid.util.NetworkUtil;

@RestrictTo(RestrictTo.Scope.LIBRARY) public class Linker extends Presenter {

  @NonNull private static final String BASE_MARKET = "market://details?id=";
  @NonNull private static final String FACEBOOK = "https://www.facebook.com/pyamsoftware";
  @NonNull private static final String GOOGLE_PLAY_DEVELOPER_PAGE =
      "https://play.google.com/store/apps/dev?id=5257476342110165153";
  @NonNull private static final String GOOGLE_PLUS =
      "https://plus.google.com/+Pyamsoft-officialBlogspot/posts";
  @NonNull private static final String OFFICIAL_BLOG = "https://pyamsoft.blogspot.com/";

  @Nullable private static volatile Linker instance;

  @NonNull private final Context context;

  private Linker(@NonNull Context context) {
    this.context = Checker.checkNonNull(context).getApplicationContext();
  }

  @CheckResult @NonNull public static Linker with(@NonNull Context context) {
    if (instance == null) {
      synchronized (Linker.class) {
        if (instance == null) {
          instance = new Linker(context);
        }
      }
    }

    return Checker.checkNonNull(instance);
  }

  public void clickAppPage(@NonNull String link) {
    NetworkUtil.newLink(context, BASE_MARKET + link);
  }

  public void clickGooglePlay() {
    NetworkUtil.newLink(context, GOOGLE_PLAY_DEVELOPER_PAGE);
  }

  public void clickGooglePlus() {
    NetworkUtil.newLink(context, GOOGLE_PLUS);
  }

  public void clickBlogger() {
    NetworkUtil.newLink(context, OFFICIAL_BLOG);
  }

  public void clickFacebook() {
    NetworkUtil.newLink(context, FACEBOOK);
  }
}
