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

package com.pyamsoft.pydroid.loader

import android.content.Context
import android.support.annotation.CheckResult
import android.support.annotation.DrawableRes
import com.pyamsoft.pydroid.loader.resource.ResourceLoader
import com.pyamsoft.pydroid.loader.resource.RxResourceLoader

object ImageLoader {

  @JvmStatic
  @CheckResult
  fun <T : GenericLoader<GenericLoader<*, *>, *>> fromLoader(loader: T): T = loader

  @JvmStatic
  @CheckResult
  fun fromResource(context: Context,
      @DrawableRes resource: Int): ResourceLoader = RxResourceLoader(context, resource)
}
