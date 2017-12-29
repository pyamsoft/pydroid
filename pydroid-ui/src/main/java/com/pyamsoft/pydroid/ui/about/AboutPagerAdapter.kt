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

package com.pyamsoft.pydroid.ui.about

import android.support.annotation.RestrictTo
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import com.pyamsoft.pydroid.base.about.AboutLibrariesModel

@RestrictTo(RestrictTo.Scope.LIBRARY) internal class AboutPagerAdapter internal constructor(
        fm: Fragment) : FragmentStatePagerAdapter(fm.childFragmentManager) {

    private val models: MutableList<AboutLibrariesModel> = ArrayList()

    fun add(model: AboutLibrariesModel) {
        models.add(model)
    }

    fun clear() {
        models.clear()
    }

    override fun getCount(): Int = models.size

    override fun getItem(position: Int): Fragment = AboutPagerFragment.newInstance(models[position])

    override fun getPageTitle(position: Int): CharSequence = models[position].name
}
