/*
 * Copyright 2016 Peter Kenji Yamanaka
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

package com.pyamsoft.ui.social;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import com.pyamsoft.pydroid.SocialMediaLoaderCallback;
import com.pyamsoft.pydroid.social.SocialMediaPresenter;
import com.pyamsoft.pydroid.util.NetworkUtil;
import com.pyamsoft.pydroid.util.PersistentCache;
import com.pyamsoft.ui.R;
import com.pyamsoft.ui.app.BaseBoundPreference;
import com.pyamsoft.ui.databinding.ViewSocialMediaBinding;
import timber.log.Timber;

public class SocialMediaPreference extends BaseBoundPreference
    implements SocialMediaPresenter.View {

  @NonNull private static final String KEY_PRESENTER =
      "__key_social_preference_presenter__no_bundle";
  @SuppressWarnings("WeakerAccess") SocialMediaPresenter presenter;
  private ViewSocialMediaBinding binding;
  private long loadedKey;

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

    binding.googlePlay.setOnClickListener(v -> presenter.clickGooglePlay());
    binding.googlePlus.setOnClickListener(v -> presenter.clickGooglePlus());
    binding.blogger.setOnClickListener(v -> presenter.clickBlogger());
    binding.facebook.setOnClickListener(v -> presenter.clickFacebook());
  }

  @Override protected void onUnbindViewHolder() {
    super.onUnbindViewHolder();
    if (binding != null) {
      binding.googlePlay.setOnClickListener(null);
      binding.googlePlus.setOnClickListener(null);
      binding.blogger.setOnClickListener(null);
      binding.facebook.setOnClickListener(null);
      binding.unbind();
    }
  }

  @Override public void onAttached() {
    super.onAttached();

    loadedKey = PersistentCache.get().load(KEY_PRESENTER, null, new SocialMediaLoaderCallback() {

      @Override public void onPersistentLoaded(@NonNull SocialMediaPresenter persist) {
        presenter = persist;
      }
    });

    presenter.bindView(this);
  }

  @Override public void onDetached() {
    super.onDetached();
    presenter.unbindView();
    PersistentCache.get().unload(loadedKey);
  }

  @Override public void onSocialMediaClicked(@NonNull String link) {
    NetworkUtil.newLink(getContext(), link);
  }
}
