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
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.helper.setOnDebouncedClickListener

class SocialMediaLayout : LinearLayout {

  private val googlePlay: View
  private val googlePlus: View
  private val blogger: View
  private val facebook: View

  constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(
      context, attrs, defStyleAttr, defStyleRes)

  constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs,
      defStyleAttr)

  constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

  constructor(context: Context) : super(context)

  init {
    orientation = HORIZONTAL
    inflate(context, R.layout.view_social_media, this)
    googlePlay = findViewById(R.id.google_play)
    googlePlus = findViewById(R.id.google_plus)
    blogger = findViewById(R.id.blogger)
    facebook = findViewById(R.id.facebook)
  }

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    googlePlay.setOnDebouncedClickListener { Linker.clickGooglePlay(googlePlay.context) }
    googlePlus.setOnDebouncedClickListener { Linker.clickGooglePlus(googlePlus.context) }
    blogger.setOnDebouncedClickListener { Linker.clickBlogger(blogger.context) }
    facebook.setOnDebouncedClickListener { Linker.clickFacebook(facebook.context) }
  }

  override fun onDetachedFromWindow() {
    super.onDetachedFromWindow()
    googlePlay.setOnDebouncedClickListener(null)
    googlePlus.setOnDebouncedClickListener(null)
    blogger.setOnDebouncedClickListener(null)
    facebook.setOnDebouncedClickListener(null)
  }

}