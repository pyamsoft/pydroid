/*
 * Copyright (C) 2018 Peter Kenji Yamanaka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pyamsoft.pydroid.base

import android.app.Application
import android.content.Context
import com.pyamsoft.pydroid.ApplicationModule
import com.pyamsoft.pydroid.data.enforceComputation
import com.pyamsoft.pydroid.data.enforceIo
import com.pyamsoft.pydroid.data.enforceMainThread
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

open class ApplicationModuleImpl(
  private val application: Application
) : ApplicationModule {

  override fun provideApplication(): Application = application

  override fun provideContext(): Context = application.applicationContext

  override fun provideIoScheduler(): Scheduler = Schedulers.io().also { it.enforceIo() }

  override fun provideComputationScheduler(): Scheduler =
    Schedulers.computation().also { it.enforceComputation() }

  override fun provideMainThreadScheduler(): Scheduler =
    AndroidSchedulers.mainThread().also { it.enforceMainThread() }
}
