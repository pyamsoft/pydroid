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

package com.pyamsoft.pydroid.loader

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.annotation.DrawableRes
import com.pyamsoft.pydroid.data.Cache
import com.pyamsoft.pydroid.loader.cache.ImageCache
import com.pyamsoft.pydroid.loader.resource.ResourceLoader
import com.pyamsoft.pydroid.loader.resource.RxResourceLoader
import io.reactivex.Scheduler

internal class ImageLoaderImpl internal constructor(private val context: Context,
        private val resourceImageCache: ImageCache<Int, Drawable>,
        private val mainThreadScheduler: Scheduler, private val ioScheduler: Scheduler) :
        ImageLoader, Cache {

    override fun clearCache() {
        resourceImageCache.clearCache()
    }

    override fun fromResource(@DrawableRes resource: Int): ResourceLoader = fromResource(resource,
            0)

    override fun fromResource(@DrawableRes resource: Int, @DrawableRes errorResource: Int): ResourceLoader = RxResourceLoader(
            context.applicationContext, resource, errorResource, resourceImageCache, mainThreadScheduler, ioScheduler)
}