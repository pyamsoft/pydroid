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

package com.pyamsoft.pydroid.ui

import android.content.ActivityNotFoundException
import android.content.Context
import com.pyamsoft.pydroid.bus.EventBus
import com.pyamsoft.pydroid.bus.RxBus
import com.pyamsoft.pydroid.ui.social.Linker

internal class UiModuleImpl internal constructor(context: Context) : UiModule {

  private val linker = Linker.create(context.applicationContext, context.packageName)
  private val linkerErrorBus = RxBus.create<ActivityNotFoundException>()

  override fun provideLinker(): Linker {
    return linker
  }

  override fun provideLinkerErrorBus(): EventBus<ActivityNotFoundException> {
    return linkerErrorBus
  }

}
