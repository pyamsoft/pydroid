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

package com.pyamsoft.pydroid.base.version

import com.pyamsoft.pydroid.data.Cache
import io.reactivex.Single
import java.util.concurrent.TimeUnit

internal class VersionCheckInteractorCache internal constructor(
        private val impl: VersionCheckInteractor) : VersionCheckInteractor, Cache {

    private var cachedResponse: Single<Int>? = null
    private var responseLastAccess: Long = 0L

    override fun checkVersion(packageName: String, force: Boolean): Single<Int> {
        return Single.defer {
            val cache = cachedResponse
            val response: Single<Int>
            val currentTime = System.currentTimeMillis()
            if (force || cache == null || responseLastAccess + THIRTY_SECONDS_MILLIS < currentTime) {
                response = impl.checkVersion(packageName, force).cache()
                cachedResponse = response
                responseLastAccess = currentTime
            } else {
                response = cache
            }
            return@defer response
        }.doOnError { clearCache() }
    }

    override fun clearCache() {
        cachedResponse = null
    }

    companion object {

        private val THIRTY_SECONDS_MILLIS = TimeUnit.SECONDS.toMillis(30L)
    }
}
