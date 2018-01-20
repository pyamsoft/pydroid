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

import android.graphics.drawable.Drawable
import android.support.annotation.CheckResult
import com.pyamsoft.pydroid.PYDroidModule
import com.pyamsoft.pydroid.data.Cache
import com.pyamsoft.pydroid.loader.cache.ImageCache
import com.pyamsoft.pydroid.loader.cache.ResourceImageCache

class LoaderModuleImpl(module: PYDroidModule) : LoaderModule {

    private val imageLoader: ImageLoaderImpl

    init {
        val resourceImageCache: ImageCache<Int, Drawable> = ResourceImageCache()
        imageLoader = ImageLoaderImpl(
            module.provideContext(), resourceImageCache,
            module.provideMainThreadScheduler(), module.provideIoScheduler()
        )
    }

    // Singleton
    @CheckResult
    override fun provideImageLoader(): ImageLoader = imageLoader

    // Singleton
    @CheckResult
    override fun provideImageLoaderCache(): Cache = imageLoader
}
