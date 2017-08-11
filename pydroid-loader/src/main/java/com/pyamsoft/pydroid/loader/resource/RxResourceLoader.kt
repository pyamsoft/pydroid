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

package com.pyamsoft.pydroid.loader.resource

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.annotation.DrawableRes
import com.pyamsoft.pydroid.helper.enforceIo
import com.pyamsoft.pydroid.helper.enforceMainThread
import com.pyamsoft.pydroid.loader.loaded.Loaded
import com.pyamsoft.pydroid.loader.loaded.RxLoaded
import com.pyamsoft.pydroid.loader.targets.Target
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class RxResourceLoader(context: Context, @DrawableRes resource: Int) : ResourceLoader(context,
    resource) {

  private val obsScheduler: Scheduler = AndroidSchedulers.mainThread()
  private val subScheduler: Scheduler = Schedulers.io()

  init {
    obsScheduler.enforceMainThread()
    subScheduler.enforceIo()
  }

  override fun load(target: Target<Drawable>, @DrawableRes resource: Int): Loaded {
    return RxLoaded(
        Single.fromCallable { loadResource(appContext) }.subscribeOn(subScheduler).observeOn(
            obsScheduler).doOnSubscribe {
          startAction(target)
        }.doOnError {
          Timber.e(it, "Error loading AsyncDrawable")
          errorAction(target)
        }.doAfterSuccess {
          completeAction(target)
        }.subscribe(target::loadImage, {
          Timber.e(it, "Error loading Drawable using RxResourceLoader")
        }))
  }
}
