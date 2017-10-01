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
import android.support.v7.preference.PreferenceViewHolder
import android.util.AttributeSet
import android.view.View
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.app.BaseBoundPreference

class SocialMediaPreference : BaseBoundPreference {

  constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(
      context, attrs, defStyleAttr, defStyleRes) {
    init()
  }

  constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs,
      defStyleAttr) {
    init()
  }

  constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    init()
  }

  constructor(context: Context) : super(context) {
    init()
  }

  private fun init() {
    layoutResource = R.layout.view_social_media
  }

  override fun onBindViewHolder(holder: PreferenceViewHolder) {
    super.onBindViewHolder(holder)
    val googlePlay: View = holder.findViewById(R.id.google_play)
    googlePlay.setOnClickListener { Linker.clickGooglePlay(googlePlay.context) }

    val googlePlus: View = holder.findViewById(R.id.google_plus)
    googlePlus.setOnClickListener { Linker.clickGooglePlus(googlePlus.context) }

    val blogger: View = holder.findViewById(R.id.blogger)
    blogger.setOnClickListener { Linker.clickBlogger(blogger.context) }

    val facebook: View = holder.findViewById(R.id.facebook)
    facebook.setOnClickListener { Linker.clickFacebook(facebook.context) }
  }

  override fun onUnbindViewHolder(holder: PreferenceViewHolder?) {
    super.onUnbindViewHolder(holder)
    if (holder != null) {
      holder.findViewById(R.id.google_play).setOnClickListener(null)
      holder.findViewById(R.id.google_plus).setOnClickListener(null)
      holder.findViewById(R.id.blogger).setOnClickListener(null)
      holder.findViewById(R.id.facebook).setOnClickListener(null)
    }
  }
}
