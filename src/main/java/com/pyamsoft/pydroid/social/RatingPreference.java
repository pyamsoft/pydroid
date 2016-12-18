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
 */

package com.pyamsoft.pydroid.social;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import com.pyamsoft.pydroid.R;
import com.pyamsoft.pydroid.SocialMediaLoaderCallback;
import com.pyamsoft.pydroid.databinding.ViewRatingButtonBinding;
import com.pyamsoft.pydroid.util.NetworkUtil;
import com.pyamsoft.pydroid.util.PersistentCache;
import timber.log.Timber;

public class RatingPreference extends Preference implements SocialMediaPresenter.View {

  @NonNull private static final String KEY_PRESENTER = "key_rate_preference_presenter";
  @SuppressWarnings("WeakerAccess") SocialMediaPresenter presenter;
  private ViewRatingButtonBinding binding;
  private long loadedKey;

  public RatingPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    init();
  }

  public RatingPreference(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  public RatingPreference(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public RatingPreference(Context context) {
    super(context);
    init();
  }

  private void init() {
    setLayoutResource(R.layout.view_rating_button);

    loadedKey = PersistentCache.get().load(KEY_PRESENTER, null, new SocialMediaLoaderCallback() {

      @Override public void onPersistentLoaded(@NonNull SocialMediaPresenter persist) {
        presenter = persist;
      }
    });
  }

  @Override public void onBindViewHolder(PreferenceViewHolder holder) {
    super.onBindViewHolder(holder);
    Timber.d("onBindViewHolder");
    unbind();
    binding = DataBindingUtil.bind(holder.itemView);

    binding.ratingButton.setOnClickListener(
        v -> presenter.clickAppPage(v.getContext().getPackageName()));
  }

  private void unbind() {
    if (binding != null) {
      binding.ratingButton.setOnClickListener(null);
      binding.unbind();
    }
  }

  @Override public void onAttached() {
    super.onAttached();
    presenter.bindView(this);
  }

  @Override public void onDetached() {
    super.onDetached();
    unbind();
    presenter.unbindView();
    PersistentCache.get().unload(loadedKey);
  }

  @Override public void onSocialMediaClicked(@NonNull String link) {
    NetworkUtil.newLink(getContext(), link);
  }
}
