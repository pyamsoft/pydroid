/*
 * Copyright 2017 Peter Kenji Yamanaka
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

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.pyamsoft.pydroid.helper.Checker;
import com.pyamsoft.pydroid.presenter.Presenter;
import com.pyamsoft.pydroid.util.NetworkUtil;

public final class Linker extends Presenter {

  @NonNull private static final String BASE_MARKET = "market://details?id=";
  @NonNull private static final String FACEBOOK = "https://www.facebook.com/pyamsoftware";
  @NonNull private static final String GOOGLE_PLAY_DEVELOPER_PAGE =
      "https://play.google.com/store/apps/dev?id=5257476342110165153";
  @NonNull private static final String GOOGLE_PLUS =
      "https://plus.google.com/+Pyamsoft-officialBlogspot/posts";
  @NonNull private static final String OFFICIAL_BLOG = "https://pyamsoft.blogspot.com/";

  @SuppressLint("StaticFieldLeak") @Nullable private static volatile Linker instance;
  @NonNull private final Context appContext;

  private Linker(@NonNull Context context) {
    this.appContext = Checker.checkNonNull(context).getApplicationContext();
  }

  @CheckResult @NonNull public static Linker with(@NonNull Context context) {
    if (instance == null) {
      synchronized (Linker.class) {
        if (instance == null) {
          instance = new Linker(context.getApplicationContext());
        }
      }
    }

    return Checker.checkNonNull(instance);
  }

  public void clickAppPage(@NonNull String link) {
    NetworkUtil.newLink(appContext, BASE_MARKET + link);
  }

  public void clickGooglePlay() {
    NetworkUtil.newLink(appContext, GOOGLE_PLAY_DEVELOPER_PAGE);
  }

  public void clickGooglePlus() {
    NetworkUtil.newLink(appContext, GOOGLE_PLUS);
  }

  public void clickBlogger() {
    NetworkUtil.newLink(appContext, OFFICIAL_BLOG);
  }

  public void clickFacebook() {
    NetworkUtil.newLink(appContext, FACEBOOK);
  }
}
