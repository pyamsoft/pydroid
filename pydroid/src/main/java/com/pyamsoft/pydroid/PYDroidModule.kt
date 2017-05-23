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
 */

package com.pyamsoft.pydroid

import android.content.Context
import android.support.annotation.CheckResult
import android.support.annotation.RestrictTo
import com.pyamsoft.pydroid.about.AboutLibrariesModel
import com.pyamsoft.pydroid.about.Licenses
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.Collections

@RestrictTo(RestrictTo.Scope.LIBRARY) class PYDroidModule(context: Context, val isDebug: Boolean) {

  // Singleton
  private val appContext: Context = context.applicationContext
  private val licenses: List<AboutLibrariesModel> = Licenses.getLicenses()
  private val preferences: PYDroidPreferencesImpl = PYDroidPreferencesImpl(appContext)

  // Singleton
  @CheckResult fun provideContext(): Context {
    return appContext
  }

  // Singleton
  @CheckResult fun provideRatingPreferences(): RatingPreferences {
    return preferences
  }

  // Singleton
  @CheckResult fun provideLicenseMap(): List<AboutLibrariesModel> {
    return Collections.unmodifiableList(licenses)
  }

  // Singleton
  @CheckResult fun provideSubScheduler(): Scheduler {
    return Schedulers.io()
  }

  // Singleton
  @CheckResult fun provideObsScheduler(): Scheduler {
    return AndroidSchedulers.mainThread()
  }
}
