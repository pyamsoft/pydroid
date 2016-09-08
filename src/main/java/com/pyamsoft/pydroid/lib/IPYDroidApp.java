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

package com.pyamsoft.pydroid.lib;

import android.app.Application;
import android.content.Context;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import dagger.Component;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import javax.inject.Singleton;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

abstract class IPYDroidApp extends Application {

  // Not an interface just to hide this provide from subclasses
  @CheckResult @NonNull abstract <T extends PYDroidComponent> T provideComponent();

  @Singleton @Component(modules = PYDroidModule.class) interface PYDroidComponent {

    AboutLibrariesComponent plusAboutLibrariesComponent();

    SocialMediaComponent plusSocialMediaComponent();

    ApiComponent plusApiComponent(ApiModule apiModule);
  }

  @Module static class PYDroidModule {

    @NonNull private final Context appContext;

    public PYDroidModule(final @NonNull Context context) {
      appContext = context.getApplicationContext();
    }

    @Singleton @Provides Context provideContext() {
      return appContext;
    }

    @Singleton @Provides @Named("io") Scheduler provideIOScheduler() {
      return Schedulers.io();
    }

    @Singleton @Provides @Named("main") Scheduler provideMainThreadScheduler() {
      return AndroidSchedulers.mainThread();
    }
  }
}
