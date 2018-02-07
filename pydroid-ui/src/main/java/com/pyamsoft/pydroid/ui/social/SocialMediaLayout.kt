/*
 * Copyright (C) 2018 Peter Kenji Yamanaka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pyamsoft.pydroid.ui.social

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.Lifecycle.Event.ON_CREATE
import android.arch.lifecycle.Lifecycle.Event.ON_DESTROY
import android.arch.lifecycle.Lifecycle.Event.ON_PAUSE
import android.arch.lifecycle.Lifecycle.Event.ON_RESUME
import android.arch.lifecycle.Lifecycle.Event.ON_START
import android.arch.lifecycle.Lifecycle.Event.ON_STOP
import android.arch.lifecycle.LifecycleOwner
import android.content.Context
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.pyamsoft.pydroid.loader.ImageLoader
import com.pyamsoft.pydroid.loader.targets.DrawableImageTarget
import com.pyamsoft.pydroid.ui.PYDroid
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.app.fragment.ViewLifecycleOwner
import com.pyamsoft.pydroid.ui.databinding.ViewSocialMediaBinding
import com.pyamsoft.pydroid.ui.helper.setOnDebouncedClickListener

class SocialMediaLayout : LinearLayout, LifecycleOwner {

  internal lateinit var imageLoader: ImageLoader
  private val binding: ViewSocialMediaBinding
  private val lifecycleOwner = ViewLifecycleOwner()

  @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
  constructor(
    context: Context,
    attrs: AttributeSet,
    defStyleAttr: Int,
    defStyleRes: Int
  ) : super(
      context, attrs, defStyleAttr, defStyleRes
  )

  constructor(
    context: Context,
    attrs: AttributeSet,
    defStyleAttr: Int
  ) : super(
      context, attrs,
      defStyleAttr
  )

  constructor(
    context: Context,
    attrs: AttributeSet
  ) : super(context, attrs)

  constructor(context: Context) : super(context)

  init {
    orientation = HORIZONTAL
    binding = ViewSocialMediaBinding.inflate(LayoutInflater.from(context), this, false)
    addView(binding.root)
    PYDroid.obtain()
        .inject(this)
  }

  override fun getLifecycle(): Lifecycle {
    return lifecycleOwner.registry
  }

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    binding.apply {
      googlePlay.setOnDebouncedClickListener { Linker.clickGooglePlay(context) }
      googlePlus.setOnDebouncedClickListener { Linker.clickGooglePlus(context) }
      blogger.setOnDebouncedClickListener { Linker.clickBlogger(context) }
      facebook.setOnDebouncedClickListener { Linker.clickFacebook(context) }
    }

    lifecycleOwner.registry.apply {
      handleLifecycleEvent(ON_CREATE)
      handleLifecycleEvent(ON_START)
      handleLifecycleEvent(ON_RESUME)
    }

    val self = this
    imageLoader.apply {
      fromResource(R.drawable.google_play).into(
          DrawableImageTarget.forImageView(binding.googlePlay)
      )
          .bind(self)
      fromResource(R.drawable.google_plus).into(
          DrawableImageTarget.forImageView(binding.googlePlus)
      )
          .bind(self)
      fromResource(R.drawable.blogger_icon).into(
          DrawableImageTarget.forImageView(binding.blogger)
      )
          .bind(self)
      fromResource(R.drawable.facebook_icon).into(
          DrawableImageTarget.forImageView(binding.facebook)
      )
          .bind(self)
    }
  }

  override fun onDetachedFromWindow() {
    super.onDetachedFromWindow()
    binding.apply {
      googlePlay.setOnDebouncedClickListener(null)
      googlePlus.setOnDebouncedClickListener(null)
      blogger.setOnDebouncedClickListener(null)
      facebook.setOnDebouncedClickListener(null)
    }

    lifecycleOwner.registry.apply {
      handleLifecycleEvent(ON_PAUSE)
      handleLifecycleEvent(ON_STOP)
      handleLifecycleEvent(ON_DESTROY)
    }
  }
}