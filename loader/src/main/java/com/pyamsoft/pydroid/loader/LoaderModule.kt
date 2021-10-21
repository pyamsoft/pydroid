/*
 * Copyright 2020 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.loader

import android.content.Context
import androidx.annotation.CheckResult

/**
 * LoaderModule is a self contained class which exposes the public API of the library in an expected
 * format. You do not need to consume the library via the module, but it is a quick easy way to get
 * started.
 */
@Deprecated("Use Landscapist in Jetpack Compose UI")
public class LoaderModule(params: Parameters) {

  private val impl = ImageLoaderImpl(params.context.applicationContext)

  /** Provide an ImageLoader instance for public consumption */
  @CheckResult
  public fun provideLoader(): ImageLoader {
    return impl
  }

  /** LoaderModule parameters */
  public data class Parameters(internal val context: Context)
}
