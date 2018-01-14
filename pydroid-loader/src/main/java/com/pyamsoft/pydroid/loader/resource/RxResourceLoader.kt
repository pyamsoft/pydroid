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
import com.pyamsoft.pydroid.ktext.enforceIo
import com.pyamsoft.pydroid.ktext.enforceMainThread
import com.pyamsoft.pydroid.loader.cache.ImageCache
import com.pyamsoft.pydroid.loader.loaded.Loaded
import com.pyamsoft.pydroid.loader.loaded.RxLoaded
import com.pyamsoft.pydroid.loader.targets.Target
import io.reactivex.Scheduler
import io.reactivex.Single
import timber.log.Timber

internal class RxResourceLoader internal constructor(
        context: Context, @DrawableRes resource: Int, @DrawableRes errorResource: Int,
        resourceImageCache: ImageCache<Int, Drawable>, private val mainThreadScheduler: Scheduler,
        private val ioScheduler: Scheduler) : ResourceLoader(
        context, resource, errorResource, resourceImageCache) {

    init {
        mainThreadScheduler.enforceMainThread()
        ioScheduler.enforceIo()
    }

    override fun load(target: Target<Drawable>, @DrawableRes resource: Int): Loaded {
        return RxLoaded(
                Single.fromCallable { loadResource() }
                        .subscribeOn(ioScheduler)
                        .observeOn(mainThreadScheduler)
                        .doOnSubscribe { startAction?.invoke() }
                        .doAfterSuccess { completeAction?.invoke(it) }
                        .doOnError {
                            Timber.e(it, "Error loading Drawable using RxResourceLoader")
                            errorAction?.invoke(it)
                        }.subscribe({ target.loadImage(it) },
                        { target.loadError(loadErrorResource()) }))
    }
}
