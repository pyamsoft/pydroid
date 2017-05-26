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

package com.pyamsoft.pydroid.ui.about

import android.os.Bundle
import android.support.annotation.CheckResult
import android.support.annotation.IdRes
import android.support.v4.app.FragmentActivity
import android.support.v4.view.ViewPager.OnPageChangeListener
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import com.pyamsoft.pydroid.about.AboutLibrariesModel
import com.pyamsoft.pydroid.about.AboutLibrariesPresenter
import com.pyamsoft.pydroid.about.AboutLibrariesPresenter.LoadCallback
import com.pyamsoft.pydroid.ui.PYDroid
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.about.AboutLibrariesFragment.BackStackState.LAST
import com.pyamsoft.pydroid.ui.about.AboutLibrariesFragment.BackStackState.NOT_LAST
import com.pyamsoft.pydroid.ui.app.fragment.ActionBarFragment
import kotlinx.android.synthetic.main.fragment_about_libraries.arrow_left
import kotlinx.android.synthetic.main.fragment_about_libraries.arrow_right
import kotlinx.android.synthetic.main.fragment_about_libraries.text_switcher
import kotlinx.android.synthetic.main.fragment_about_libraries.view_pager
import timber.log.Timber


class AboutLibrariesFragment : ActionBarFragment() {

  internal lateinit var presenter: AboutLibrariesPresenter
  internal lateinit var pagerAdapter: AboutPagerAdapter
  private var lastOnBackStack: Boolean = false

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val backStackStateName = arguments.getString(KEY_BACK_STACK,
        null) ?: throw NullPointerException("BackStateState is NULL")
    val backStackState = BackStackState.valueOf(backStackStateName)
    when (backStackState) {
      LAST -> lastOnBackStack = true
      NOT_LAST -> lastOnBackStack = false
      else -> throw RuntimeException("Invalid back stack state: " + backStackStateName)
    }

    PYDroid.get().provideComponent().plusAboutLibrariesComponent().inject(this)
  }

  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
      savedInstanceState: Bundle?): View? {
    return inflater?.inflate(R.layout.fragment_about_libraries, container, false)
  }

  override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    pagerAdapter = AboutPagerAdapter(this)
    view_pager.adapter = pagerAdapter
    view_pager.offscreenPageLimit = 1

    arrow_left.setOnClickListener { view_pager.arrowScroll(View.FOCUS_LEFT) }
    arrow_right.setOnClickListener { view_pager.arrowScroll(View.FOCUS_RIGHT) }

    text_switcher.setFactory {
      val text = TextView(activity)
      text.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
      text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16F)
      text
    }

    view_pager.addOnPageChangeListener(object : OnPageChangeListener {
      override fun onPageScrollStateChanged(state: Int) {
      }

      override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
      }

      override fun onPageSelected(position: Int) {
        text_switcher.setText(pagerAdapter.getPageTitle(position))
      }

    })

    text_switcher.inAnimation = AnimationUtils.loadAnimation(activity, android.R.anim.slide_in_left)
    text_switcher.outAnimation = AnimationUtils.loadAnimation(activity,
        android.R.anim.slide_out_right)

    presenter.loadLicenses(object : LoadCallback {
      override fun onLicenseLoaded(model: AboutLibrariesModel) {
        Timber.d("Load license: %s", model)
        pagerAdapter.add(model)
      }

      override fun onAllLoaded() {
        Timber.d("All licenses loaded")
        pagerAdapter.notifyDataSetChanged()
        text_switcher.setText(pagerAdapter.getPageTitle(view_pager.currentItem))
      }
    })
  }

  override fun onStop() {
    super.onStop()
    presenter.stop()
  }

  override fun onDestroy() {
    super.onDestroy()
    presenter.destroy()
  }

  override fun onResume() {
    super.onResume()
    setActionBarUpEnabled(true)
  }

  override fun onDestroyView() {
    super.onDestroyView()
    if (lastOnBackStack) {
      Timber.d("About is last on backstack, set up false")
      setActionBarUpEnabled(false)
    }
  }


  enum class BackStackState {
    LAST, NOT_LAST
  }

  companion object {

    const val TAG = "AboutLibrariesFragment"
    private const val KEY_BACK_STACK = "key_back_stack"

    @JvmStatic fun show(activity: FragmentActivity, @IdRes containerResId: Int,
        backStackState: BackStackState) {
      val fragmentManager = activity.supportFragmentManager
      if (fragmentManager.findFragmentByTag(TAG) == null) {
        fragmentManager.beginTransaction().replace(containerResId, newInstance(backStackState),
            TAG).addToBackStack(null).commit()
      }
    }

    @JvmStatic @CheckResult private fun newInstance(
        backStackState: BackStackState): AboutLibrariesFragment {
      val args = Bundle()
      val fragment = AboutLibrariesFragment()

      args.putString(KEY_BACK_STACK, backStackState.name)
      fragment.arguments = args
      return fragment
    }

  }
}

