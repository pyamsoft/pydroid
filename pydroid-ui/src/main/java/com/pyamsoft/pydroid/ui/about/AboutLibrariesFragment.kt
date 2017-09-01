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

import android.database.DataSetObserver
import android.os.Bundle
import android.support.annotation.CheckResult
import android.support.annotation.IdRes
import android.support.v4.app.FragmentActivity
import android.support.v4.view.ViewPager
import android.support.v4.view.ViewPager.OnPageChangeListener
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pyamsoft.pydroid.about.AboutLibrariesModel
import com.pyamsoft.pydroid.about.AboutLibrariesPresenter
import com.pyamsoft.pydroid.about.AboutLibrariesPresenter.LoadCallback
import com.pyamsoft.pydroid.loader.ImageLoader
import com.pyamsoft.pydroid.loader.LoaderMap
import com.pyamsoft.pydroid.presenter.Presenter
import com.pyamsoft.pydroid.ui.PYDroid
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.about.AboutLibrariesFragment.BackStackState.LAST
import com.pyamsoft.pydroid.ui.about.AboutLibrariesFragment.BackStackState.NOT_LAST
import com.pyamsoft.pydroid.ui.app.fragment.DisposableFragment
import com.pyamsoft.pydroid.ui.databinding.FragmentAboutLibrariesBinding
import timber.log.Timber


class AboutLibrariesFragment : DisposableFragment(), LoadCallback {

  internal lateinit var presenter: AboutLibrariesPresenter
  internal lateinit var pagerAdapter: AboutPagerAdapter
  private var lastOnBackStack: Boolean = false
  private val mapper = LoaderMap()
  private var listener: ViewPager.OnPageChangeListener? = null
  private lateinit var binding: FragmentAboutLibrariesBinding

  override fun provideBoundPresenters(): List<Presenter<*>> = listOf(presenter)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val backStackStateName = arguments.getString(KEY_BACK_STACK,
        null) ?: throw NullPointerException("BackStateState is NULL")
    val backStackState = BackStackState.valueOf(backStackStateName)
    lastOnBackStack = when (backStackState) {
      LAST -> true
      NOT_LAST -> false
    }

    PYDroid.with {
      it.inject(this)
    }
  }

  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
      savedInstanceState: Bundle?): View? {
    binding = FragmentAboutLibrariesBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    setupViewPager(savedInstanceState)
    setupArrows()

    presenter.bind(this)
  }

  override fun onLicenseLoaded(model: AboutLibrariesModel) {
    pagerAdapter.add(model)
  }

  override fun onAllLoaded() {
    pagerAdapter.notifyDataSetChanged()
    binding.aboutTitle.text = pagerAdapter.getPageTitle(binding.viewPager.currentItem)
  }

  private fun setupViewPager(savedInstanceState: Bundle?) {
    pagerAdapter = AboutPagerAdapter(this)
    binding.viewPager.adapter = pagerAdapter
    binding.viewPager.offscreenPageLimit = 1

    // We must observe the data set for when it finishes.
    // There is a race condition that can cause the UI to crash if pager.setCurrentItem is
    // called before notifyDataSetChanged finishes
    pagerAdapter.registerDataSetObserver(object : DataSetObserver() {

      override fun onChanged() {
        super.onChanged()
        Timber.d("Data set changed! Set current item and unregister self")
        pagerAdapter.unregisterDataSetObserver(this)

        // Reload the last looked at page
        if (savedInstanceState != null) {
          binding.viewPager.setCurrentItem(savedInstanceState.getInt(KEY_PAGE, 0), false)
        }
      }
    })

    listener = object : OnPageChangeListener {
      override fun onPageScrollStateChanged(state: Int) {
      }

      override fun onPageScrolled(position: Int, positionOffset: Float,
          positionOffsetPixels: Int) {
      }

      override fun onPageSelected(position: Int) {
        binding.aboutTitle.text = pagerAdapter.getPageTitle(position)
      }
    }

    binding.viewPager.addOnPageChangeListener(listener)
  }


  private fun setupArrows() {
    mapper.put("left",
        ImageLoader.fromResource(context, R.drawable.ic_arrow_down_24dp).into(binding.arrowLeft))
    binding.arrowLeft.rotation = 90F
    binding.arrowLeft.setOnClickListener {
      binding.viewPager.arrowScroll(View.FOCUS_LEFT)
    }

    mapper.put("right",
        ImageLoader.fromResource(context, R.drawable.ic_arrow_down_24dp).into(binding.arrowRight))
    binding.arrowRight.rotation = -90F
    binding.arrowRight.setOnClickListener {
      binding.viewPager.arrowScroll(View.FOCUS_RIGHT)
    }
  }

  override fun onResume() {
    super.onResume()
    setActionBarUpEnabled(true)
    setActionBarTitle("Open Source Licenses")
  }

  override fun onDestroyView() {
    super.onDestroyView()
    mapper.clear()
    if (lastOnBackStack) {
      Timber.d("About is last on backstack, set up false")
      setActionBarUpEnabled(false)
    }

    binding.viewPager.removeOnPageChangeListener(listener)
    binding.viewPager.adapter = null
    pagerAdapter.clear()

    binding.arrowLeft.setOnClickListener(null)
    binding.arrowRight.setOnClickListener(null)
  }

  override fun onSaveInstanceState(outState: Bundle?) {
    outState?.putInt(KEY_PAGE, binding.viewPager.currentItem)
    super.onSaveInstanceState(outState)
  }

  enum class BackStackState {
    LAST, NOT_LAST
  }

  companion object {

    const val TAG = "AboutLibrariesFragment"
    private const val KEY_BACK_STACK = "key_back_stack"
    private const val KEY_PAGE = "key_current_page"

    @JvmStatic
    fun show(activity: FragmentActivity, @IdRes containerResId: Int,
        backStackState: BackStackState) {
      val fragmentManager = activity.supportFragmentManager
      if (fragmentManager.findFragmentByTag(TAG) == null) {
        fragmentManager.beginTransaction().replace(containerResId, newInstance(backStackState),
            TAG).addToBackStack(null).commit()
      }
    }

    @JvmStatic
    @CheckResult private fun newInstance(
        backStackState: BackStackState): AboutLibrariesFragment {
      val args = Bundle()
      val fragment = AboutLibrariesFragment()

      args.putString(KEY_BACK_STACK, backStackState.name)
      fragment.arguments = args
      return fragment
    }

  }
}

