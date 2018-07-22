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

import android.content.ActivityNotFoundException
import android.content.Context
import android.util.AttributeSet
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle.Event.ON_CREATE
import androidx.lifecycle.Lifecycle.Event.ON_DESTROY
import androidx.lifecycle.Lifecycle.Event.ON_PAUSE
import androidx.lifecycle.Lifecycle.Event.ON_RESUME
import androidx.lifecycle.Lifecycle.Event.ON_START
import androidx.lifecycle.Lifecycle.Event.ON_STOP
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import com.pyamsoft.pydroid.core.bus.Publisher
import com.pyamsoft.pydroid.loader.ImageLoader
import com.pyamsoft.pydroid.ui.PYDroid
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.databinding.PreferenceSocialMediaBinding
import com.pyamsoft.pydroid.ui.util.setOnDebouncedClickListener
import timber.log.Timber

class SocialMediaPreference : Preference, LifecycleOwner {

  internal lateinit var linker: Linker
  internal lateinit var linkerErrorPublisher: Publisher<ActivityNotFoundException>
  internal lateinit var imageLoader: ImageLoader
  private var binding: PreferenceSocialMediaBinding? = null
  private val registry = LifecycleRegistry(this)

  constructor(
    context: Context,
    attrs: AttributeSet,
    defStyleAttr: Int
  ) : super(context, attrs, defStyleAttr)

  constructor(
    context: Context,
    attrs: AttributeSet
  ) : super(context, attrs)

  constructor(context: Context) : super(context)

  init {
    layoutResource = R.layout.preference_social_media
    PYDroid.obtain(context.applicationContext)
        .inject(this)
  }

  override fun getLifecycle(): Lifecycle {
    return registry
  }

  override fun onBindViewHolder(holder: PreferenceViewHolder) {
    super.onBindViewHolder(holder)
    val view = holder.itemView
    val bind = PreferenceSocialMediaBinding.bind(view)

    bind.apply {
      googlePlay.setOnDebouncedClickListener {
        linker.clickGooglePlay { linkerErrorPublisher.publish(it) }
      }

      googlePlus.setOnDebouncedClickListener {
        linker.clickGooglePlus { linkerErrorPublisher.publish(it) }
      }

      blogger.setOnDebouncedClickListener {
        linker.clickBlogger { linkerErrorPublisher.publish(it) }
      }

      facebook.setOnDebouncedClickListener {
        linker.clickFacebook { linkerErrorPublisher.publish(it) }
      }
    }

    imageLoader.also {
      it.fromResource(R.drawable.google_play)
          .into(bind.googlePlay)
          .bind(this)

      it.fromResource(R.drawable.google_plus)
          .into(bind.googlePlus)
          .bind(this)

      it.fromResource(R.drawable.blogger_icon)
          .into(bind.blogger)
          .bind(this)

      it.fromResource(R.drawable.facebook_icon)
          .into(bind.facebook)
          .bind(this)
    }

    binding = bind
    registry.apply {
      handleLifecycleEvent(ON_CREATE)
      handleLifecycleEvent(ON_START)
      handleLifecycleEvent(ON_RESUME)
    }
  }

  override fun onPrepareForRemoval() {
    super.onPrepareForRemoval()
    Timber.d("onPrepareForRemoval")
    binding?.apply {
      googlePlay.setOnDebouncedClickListener(null)
      googlePlus.setOnDebouncedClickListener(null)
      blogger.setOnDebouncedClickListener(null)
      facebook.setOnDebouncedClickListener(null)
      unbind()
    }
    binding = null

    registry.apply {
      handleLifecycleEvent(ON_PAUSE)
      handleLifecycleEvent(ON_STOP)
      handleLifecycleEvent(ON_DESTROY)
    }
  }

}
