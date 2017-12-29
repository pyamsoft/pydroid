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

package com.pyamsoft.pydroid.ui.social

import android.content.Context
import com.pyamsoft.pydroid.util.NetworkUtil

object Linker {

    private const val BASE_MARKET = "market://details?id="
    private const val FACEBOOK = "https://www.facebook.com/pyamsoftware"
    private const val GOOGLE_PLAY_DEVELOPER_PAGE = "https://play.google.com/store/apps/dev?id=5257476342110165153"
    private const val GOOGLE_PLUS = "https://plus.google.com/+Pyamsoft-officialBlogspot/posts"
    private const val OFFICIAL_BLOG = "https://pyamsoft.blogspot.com/"

    @JvmStatic
    fun clickAppPage(context: Context, link: String) {
        NetworkUtil.newLink(context.applicationContext, BASE_MARKET + link)
    }

    @JvmStatic
    fun clickGooglePlay(context: Context) {
        NetworkUtil.newLink(context.applicationContext, GOOGLE_PLAY_DEVELOPER_PAGE)
    }

    @JvmStatic
    fun clickGooglePlus(context: Context) {
        NetworkUtil.newLink(context.applicationContext, GOOGLE_PLUS)
    }

    @JvmStatic
    fun clickBlogger(context: Context) {
        NetworkUtil.newLink(context.applicationContext, OFFICIAL_BLOG)
    }

    @JvmStatic
    fun clickFacebook(context: Context) {
        NetworkUtil.newLink(context.applicationContext, FACEBOOK)
    }
}
