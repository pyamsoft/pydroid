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
import android.support.annotation.CheckResult
import android.support.annotation.DrawableRes
import android.support.v7.content.res.AppCompatResources
import android.widget.ImageView
import com.pyamsoft.pydroid.loader.GenericLoader
import com.pyamsoft.pydroid.loader.cache.ImageCache
import com.pyamsoft.pydroid.loader.cache.ImageCache.ImageCacheKey
import com.pyamsoft.pydroid.loader.loaded.Loaded
import com.pyamsoft.pydroid.loader.targets.DrawableImageTarget
import com.pyamsoft.pydroid.loader.targets.Target
import com.pyamsoft.pydroid.util.DrawableUtil

/**
 * Loads Images from Resources.
 *
 * Supports Drawable resource types, is not threaded
 */
abstract class ResourceLoader protected constructor(context: Context,
        @param:DrawableRes private val resource: Int, @param:DrawableRes private val errorResource: Int,
        private val resourceImageCache: ImageCache<Int, Drawable>) : GenericLoader<Drawable>() {

    @CheckResult
    private fun Int.toKey(): ImageCacheKey<Int> = ImageCacheKey(this)

    private val appContext: Context = context.applicationContext

    init {
        if (this.resource == 0) {
            throw IllegalStateException("No resource to load")
        }
    }

    final override fun into(imageView: ImageView): Loaded =
            into(DrawableImageTarget.forImageView(imageView))

    final override fun into(target: Target<Drawable>): Loaded = load(target, resource)

    @CheckResult protected fun loadResource(): Drawable {
        val key: ImageCacheKey<Int> = resource.toKey()
        val cached: Drawable? = resourceImageCache.retrieve(key)
        if (cached == null) {
            val result = loadFreshResource()
            resourceImageCache.cache(key, result)
            return result
        } else {
            return cached
        }
    }

    @CheckResult
    private fun loadFreshResource(): Drawable {
        val possiblyLoaded: Drawable = AppCompatResources.getDrawable(appContext, resource)!!
        if (tint != 0) {
            // Return
            return DrawableUtil.tintDrawableFromRes(appContext, possiblyLoaded, tint)
        } else {
            // Return
            return possiblyLoaded
        }
    }

    @CheckResult protected fun loadErrorResource(): Drawable? {
        val key: ImageCacheKey<Int> = errorResource.toKey()
        val cached: Drawable? = resourceImageCache.retrieve(key)
        if (cached == null) {
            val result: Drawable? = loadFreshErrorResource()
            if (result != null) {
                resourceImageCache.cache(key, result)
            }
            return result
        } else {
            return cached
        }
    }

    @CheckResult private fun loadFreshErrorResource(): Drawable? {
        errorResource.let {
            if (it == 0) {
                return null
            } else {
                return AppCompatResources.getDrawable(appContext, it)
            }
        }
    }

    @CheckResult protected abstract fun load(target: Target<Drawable>,
            @DrawableRes resource: Int): Loaded
}
