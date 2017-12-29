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

package com.pyamsoft.pydroid.base.about

import com.pyamsoft.pydroid.data.Cache
import io.reactivex.Observable

internal class AboutLibrariesInteractorCache internal constructor(
        private val impl: AboutLibrariesInteractor) : AboutLibrariesInteractor, Cache {

    private var cachedLicenses: Observable<AboutLibrariesModel>? = null

    override fun loadLicenses(force: Boolean): Observable<AboutLibrariesModel> {
        return Observable.defer {
            val cache = cachedLicenses
            val licenses: Observable<AboutLibrariesModel>
            if (force || cache == null) {
                licenses = impl.loadLicenses(force).cache()
                cachedLicenses = licenses
            } else {
                licenses = cache
            }
            return@defer licenses
        }.doOnError { clearCache() }
    }

    override fun clearCache() {
        cachedLicenses = null
    }
}
