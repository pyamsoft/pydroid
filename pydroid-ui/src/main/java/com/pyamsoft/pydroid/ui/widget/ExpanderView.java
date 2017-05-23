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
 */

package com.pyamsoft.pydroid.ui.widget;

import android.content.Context;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.CheckResult;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Px;
import android.support.annotation.RequiresApi;
import android.support.annotation.StringRes;
import android.support.annotation.StyleRes;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.pyamsoft.pydroid.loader.ImageLoader;
import com.pyamsoft.pydroid.loader.LoaderHelper;
import com.pyamsoft.pydroid.loader.loaded.Loaded;
import com.pyamsoft.pydroid.ui.R;
import com.pyamsoft.pydroid.ui.databinding.ViewExpanderBinding;
import timber.log.Timber;

public class ExpanderView extends FrameLayout {

  ViewExpanderBinding binding;
  boolean expanded;
  @NonNull Loaded arrowLoad = LoaderHelper.Companion.empty();
  @Nullable ViewPropertyAnimatorCompat arrowAnimation;
  @Nullable ViewPropertyAnimatorCompat containerAnimation;

  public ExpanderView(@NonNull Context context) {
    super(context);
    init();
  }

  public ExpanderView(@NonNull Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public ExpanderView(@NonNull Context context, @Nullable AttributeSet attrs,
      @AttrRes int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  public ExpanderView(@NonNull Context context, @Nullable AttributeSet attrs,
      @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    init();
  }

  private void init() {
    if (isInEditMode()) {
      Timber.d("In edit mode!");
      addView(new LinearLayout(getContext()));
      return;
    }

    binding = ViewExpanderBinding.inflate(LayoutInflater.from(getContext()), this, false);
    addView(binding.getRoot());

    cancelArrowAnimation();
    cancelContainerAnimation();
    if (expanded) {
      ViewCompat.setRotation(binding.expanderArrow, 0);
      binding.expanderContainer.setAlpha(1);
      //binding.expanderContainer.setScaleY(1);
      binding.expanderContainer.setVisibility(View.VISIBLE);
    } else {
      ViewCompat.setRotation(binding.expanderArrow, 180);
      binding.expanderContainer.setVisibility(View.GONE);
      binding.expanderContainer.setAlpha(0);
      //binding.expanderContainer.setScaleY(0);
    }

    binding.expanderContainer.setVisibility(expanded ? View.VISIBLE : View.GONE);
    binding.expanderTitleContainer.setOnClickListener(v -> {
      expanded = !expanded;
      cancelArrowAnimation();
      arrowAnimation = ViewCompat.animate(binding.expanderArrow).rotation(expanded ? 0 : 180);
      arrowAnimation.start();

      cancelContainerAnimation();
      if (expanded) {
        // This is expanding now
        // Be visible, but hidden
        binding.expanderContainer.setAlpha(0);

        // TODO Animation is buggy
        //binding.expanderContainer.setScaleY(0);
        containerAnimation = ViewCompat.animate(binding.expanderContainer)
            .alpha(1)
            .setListener(new ViewPropertyAnimatorListenerAdapter() {

              @Override public void onAnimationStart(View view) {
                view.setVisibility(View.VISIBLE);
              }

              @Override public void onAnimationEnd(View view) {
                view.setVisibility(View.VISIBLE);
              }
            });
        containerAnimation.start();
      } else {
        // This is collapsing now
        // Be visible
        binding.expanderContainer.setAlpha(1);

        // TODO Animation is buggy
        //binding.expanderContainer.setScaleY(1);
        containerAnimation = ViewCompat.animate(binding.expanderContainer)
            .alpha(0)
            .setListener(new ViewPropertyAnimatorListenerAdapter() {

              @Override public void onAnimationStart(View view) {
                view.setVisibility(View.VISIBLE);
              }

              @Override public void onAnimationEnd(View view) {
                view.setVisibility(View.GONE);
              }
            });
        containerAnimation.start();
      }
    });
  }

  void cancelArrowAnimation() {
    if (arrowAnimation != null) {
      arrowAnimation.cancel();
      arrowAnimation = null;
    }
  }

  void cancelContainerAnimation() {
    if (containerAnimation != null) {
      containerAnimation.cancel();
      containerAnimation = null;
    }
  }

  @Override protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    if (isInEditMode()) {
      Timber.d("In edit mode!");
      return;
    }

    arrowLoad = LoaderHelper.Companion.unload(arrowLoad);
    arrowLoad = ImageLoader.Companion.fromResource(getContext(), R.drawable.ic_arrow_up_24dp)
        .into(binding.expanderArrow);
  }

  @Override protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    if (isInEditMode()) {
      Timber.d("In edit mode!");
      return;
    }

    arrowLoad = LoaderHelper.Companion.unload(arrowLoad);
    cancelArrowAnimation();
    cancelContainerAnimation();
  }

  @CheckResult @NonNull public TextView editTitleView() {
    return binding.expanderTitle;
  }

  public void setTitle(@NonNull String title) {
    setTitle(new SpannableString(title));
  }

  public void setTitle(@StringRes int title) {
    setTitle(new SpannableString(getContext().getString(title)));
  }

  public void setTitle(@NonNull Spannable title) {
    binding.expanderTitle.setText(title);
    binding.expanderTitle.setVisibility(View.VISIBLE);
  }

  public void clearTitle() {
    binding.expanderTitle.setText(null);
    binding.expanderTitle.setVisibility(View.GONE);
  }

  public void setTitleTextSize(@Px int size) {
    binding.expanderTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
  }

  @CheckResult @NonNull public TextView editDescriptionView() {
    return binding.expanderDescription;
  }

  public void setDescription(@NonNull String description) {
    setDescription(new SpannableString(description));
  }

  public void setDescription(@StringRes int description) {
    setDescription(new SpannableString(getContext().getString(description)));
  }

  public void setDescription(@NonNull Spannable description) {
    binding.expanderDescription.setText(description);
    binding.expanderDescription.setVisibility(View.VISIBLE);
  }

  public void clearDescription() {
    binding.expanderDescription.setText(null);
    binding.expanderDescription.setVisibility(View.GONE);
  }

  public void setExpandingContent(@LayoutRes int layout) {
    setExpandingContent(LayoutInflater.from(getContext()).inflate(layout, this, false));
  }

  public void setExpandingContent(@NonNull View view) {
    binding.expanderContainer.addView(view);
  }
}
