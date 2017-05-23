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

package com.pyamsoft.pydroid.ui.widget

import android.content.Context
import android.os.Build
import android.support.annotation.AttrRes
import android.support.annotation.CheckResult
import android.support.annotation.LayoutRes
import android.support.annotation.Px
import android.support.annotation.RequiresApi
import android.support.annotation.StringRes
import android.support.annotation.StyleRes
import android.support.v4.view.ViewCompat
import android.support.v4.view.ViewPropertyAnimatorCompat
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter
import android.text.Spannable
import android.text.SpannableString
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.pyamsoft.pydroid.loader.ImageLoader
import com.pyamsoft.pydroid.loader.LoaderHelper
import com.pyamsoft.pydroid.ui.R
import kotlinx.android.synthetic.main.view_expander.view.expander_arrow
import kotlinx.android.synthetic.main.view_expander.view.expander_container
import kotlinx.android.synthetic.main.view_expander.view.expander_description
import kotlinx.android.synthetic.main.view_expander.view.expander_title
import kotlinx.android.synthetic.main.view_expander.view.expander_title_container
import timber.log.Timber

class ExpanderView : FrameLayout {

  internal var expanded: Boolean = false
  internal var arrowLoad = LoaderHelper.empty()
  internal var arrowAnimation: ViewPropertyAnimatorCompat? = null
  internal var containerAnimation: ViewPropertyAnimatorCompat? = null
  internal lateinit var binding: View

  constructor(context: Context) : super(context) {
    init()
  }

  constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
    init()
  }

  constructor(context: Context, attrs: AttributeSet?, @AttrRes defStyleAttr: Int) : super(context,
      attrs, defStyleAttr) {
    init()
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP) constructor(context: Context,
      attrs: AttributeSet?, @AttrRes defStyleAttr: Int, @StyleRes defStyleRes: Int) : super(context,
      attrs, defStyleAttr, defStyleRes) {
    init()
  }

  private fun init() {
    if (isInEditMode) {
      Timber.d("In edit mode!")
      addView(LinearLayout(context))
      return
    }

    binding = LayoutInflater.from(context).inflate(R.layout.view_expander, this, false)
    addView(binding)

    cancelArrowAnimation()
    cancelContainerAnimation()
    if (expanded) {
      ViewCompat.setRotation(binding.expander_arrow, 0f)
      binding.expander_container.alpha = 1F
      //binding.expanderContainer.setScaleY(1);
      binding.expander_container.visibility = View.VISIBLE
    } else {
      ViewCompat.setRotation(binding.expander_arrow, 180f)
      binding.expander_container.visibility = View.GONE
      binding.expander_container.alpha = 0F
      //binding.expanderContainer.setScaleY(0);
    }

    binding.expander_container.visibility = if (expanded) View.VISIBLE else View.GONE
    binding.expander_title_container.setOnClickListener({
      expanded = !expanded
      cancelArrowAnimation()
      arrowAnimation = ViewCompat.animate(binding.expander_arrow).rotation(
          if (expanded) 0F else 180F)
      arrowAnimation!!.start()

      cancelContainerAnimation()
      if (expanded) {
        // This is expanding now
        // Be visible, but hidden
        binding.expander_container.alpha = 0F

        // TODO Animation is buggy
        //binding.expanderContainer.setScaleY(0);
        containerAnimation = ViewCompat.animate(binding.expander_container).alpha(1F).setListener(
            object : ViewPropertyAnimatorListenerAdapter() {

              override fun onAnimationStart(view: View?) {
                view!!.visibility = View.VISIBLE
              }

              override fun onAnimationEnd(view: View?) {
                view!!.visibility = View.VISIBLE
              }
            })
        containerAnimation!!.start()
      } else {
        // This is collapsing now
        // Be visible
        binding.expander_container.alpha = 1F

        // TODO Animation is buggy
        //binding.expanderContainer.setScaleY(1);
        containerAnimation = ViewCompat.animate(binding.expander_container).alpha(0F).setListener(
            object : ViewPropertyAnimatorListenerAdapter() {

              override fun onAnimationStart(view: View?) {
                view!!.visibility = View.VISIBLE
              }

              override fun onAnimationEnd(view: View?) {
                view!!.visibility = View.GONE
              }
            })
        containerAnimation!!.start()
      }
    })
  }

  internal fun cancelArrowAnimation() {
    if (arrowAnimation != null) {
      arrowAnimation!!.cancel()
      arrowAnimation = null
    }
  }

  internal fun cancelContainerAnimation() {
    if (containerAnimation != null) {
      containerAnimation!!.cancel()
      containerAnimation = null
    }
  }

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    if (isInEditMode) {
      Timber.d("In edit mode!")
      return
    }

    arrowLoad = LoaderHelper.unload(arrowLoad)
    arrowLoad = ImageLoader.fromResource(context, R.drawable.ic_arrow_up_24dp).into(
        binding.expander_arrow)
  }

  override fun onDetachedFromWindow() {
    super.onDetachedFromWindow()
    if (isInEditMode) {
      Timber.d("In edit mode!")
      return
    }

    arrowLoad = LoaderHelper.unload(arrowLoad)
    cancelArrowAnimation()
    cancelContainerAnimation()
  }

  @CheckResult fun editTitleView(): TextView {
    return binding.expander_title
  }

  fun setTitle(title: String) {
    setTitle(SpannableString(title))
  }

  fun setTitle(@StringRes title: Int) {
    setTitle(SpannableString(context.getString(title)))
  }

  fun setTitle(title: Spannable) {
    binding.expander_title.text = title
    binding.expander_title.visibility = View.VISIBLE
  }

  fun clearTitle() {
    binding.expander_title.text = null
    binding.expander_title.visibility = View.GONE
  }

  fun setTitleTextSize(@Px size: Int) {
    binding.expander_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, size.toFloat())
  }

  @CheckResult fun editDescriptionView(): TextView {
    return binding.expander_description
  }

  fun setDescription(description: String) {
    setDescription(SpannableString(description))
  }

  fun setDescription(@StringRes description: Int) {
    setDescription(SpannableString(context.getString(description)))
  }

  fun setDescription(description: Spannable) {
    binding.expander_description.text = description
    binding.expander_description.visibility = View.VISIBLE
  }

  fun clearDescription() {
    binding.expander_description.text = null
    binding.expander_description.visibility = View.GONE
  }

  fun setExpandingContent(@LayoutRes layout: Int) {
    setExpandingContent(LayoutInflater.from(context).inflate(layout, this, false))
  }

  fun setExpandingContent(view: View) {
    binding.expander_container.addView(view)
  }
}
