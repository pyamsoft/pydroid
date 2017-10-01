/*
 *     Copyright (C) 2017 Peter Kenji Yamanaka
 *
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
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
        Single.fromCallable { loadResource() }.subscribeOn(subScheduler).observeOn(obsScheduler)
            .doOnSubscribe { startAction() }
            .doOnError {
              Timber.e(it, "Error loading AsyncDrawable")
              errorAction(it)
            }.doAfterSuccess { completeAction(it) }
            .subscribe(target::loadImage, {
              Timber.e(it, "Error loading Drawable using RxResourceLoader")
            }))
  }
}
