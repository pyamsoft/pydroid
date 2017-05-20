/*
 * Copyright 2017 Peter Kenji Yamanaka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.pyamsoft.pydroid.ui.social;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import com.pyamsoft.pydroid.ui.R;
import com.pyamsoft.pydroid.ui.app.BaseBoundPreference;
import com.pyamsoft.pydroid.ui.databinding.ViewSocialMediaBinding;
import timber.log.Timber;

public class SocialMediaPreference extends BaseBoundPreference {

  private ViewSocialMediaBinding binding;

  public SocialMediaPreference(Context context, AttributeSet attrs, int defStyleAttr,
      int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    init();
  }

  public SocialMediaPreference(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  public SocialMediaPreference(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public SocialMediaPreference(Context context) {
    super(context);
    init();
  }

  private void init() {
    setLayoutResource(R.layout.view_social_media);
  }

  @Override public void onBindViewHolder(PreferenceViewHolder holder) {
    super.onBindViewHolder(holder);
    Timber.d("onBindViewHolder");
    binding = DataBindingUtil.bind(holder.itemView);
    binding.googlePlay.setOnClickListener(v -> Linker.with(v.getContext()).clickGooglePlay());
    binding.googlePlus.setOnClickListener(v -> Linker.with(v.getContext()).clickGooglePlus());
    binding.blogger.setOnClickListener(v -> Linker.with(v.getContext()).clickBlogger());
    binding.facebook.setOnClickListener(v -> Linker.with(v.getContext()).clickFacebook());
  }

  @Override protected void onUnbindViewHolder() {
    super.onUnbindViewHolder();
    if (binding != null) {
      binding.googlePlay.setOnClickListener(null);
      binding.googlePlus.setOnClickListener(null);
      binding.blogger.setOnClickListener(null);
      binding.facebook.setOnClickListener(null);
      binding.unbind();
      binding = null;
    }
  }
}
