/*
 * Copyright 2021 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.loader.glide.loader

import android.content.Context
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager
import com.pyamsoft.pydroid.loader.glide.GlideLoader

@Deprecated("Use Landscapist in Jetpack Compose UI")
internal abstract class GlideRequestTransformer<T : Any>
protected constructor(
    context: Context,
    private val transformer: (RequestManager) -> RequestBuilder<T>
) : GlideLoader<T>(context) {

  final override fun createRequest(request: RequestManager): RequestBuilder<T> {
    val builder = transformer(request)
    return onCreateRequest(builder)
  }

  protected abstract fun onCreateRequest(builder: RequestBuilder<T>): RequestBuilder<T>
}
