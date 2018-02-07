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

package com.pyamsoft.pydroid.ui.about

import android.database.DataSetObserver
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.view.ViewPager
import android.support.v4.view.ViewPager.OnPageChangeListener
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pyamsoft.pydroid.base.about.AboutLibrariesModel
import com.pyamsoft.pydroid.base.about.AboutLibrariesPresenter
import com.pyamsoft.pydroid.loader.ImageLoader
import com.pyamsoft.pydroid.ui.PYDroid
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.app.fragment.ToolbarFragment
import com.pyamsoft.pydroid.ui.databinding.FragmentAboutLibrariesBinding
import com.pyamsoft.pydroid.ui.helper.postWith
import com.pyamsoft.pydroid.ui.helper.setOnDebouncedClickListener
import com.pyamsoft.pydroid.ui.util.setUpEnabled
import com.pyamsoft.pydroid.ui.widget.RefreshLatch
import timber.log.Timber

class AboutLibrariesFragment : ToolbarFragment(), AboutLibrariesPresenter.View {

  internal lateinit var presenter: AboutLibrariesPresenter
  internal lateinit var imageLoader: ImageLoader
  internal lateinit var pagerAdapter: AboutPagerAdapter
  private lateinit var listener: ViewPager.OnPageChangeListener
  private lateinit var binding: FragmentAboutLibrariesBinding
  private lateinit var refreshLatch: RefreshLatch

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    PYDroid.obtain()
        .inject(this)
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    binding = FragmentAboutLibrariesBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)
    refreshLatch = RefreshLatch.create(viewLifecycle) {
      binding.apply {
        if (it) {
          progressSpinner.visibility = View.VISIBLE
          viewPager.visibility = View.INVISIBLE
          aboutTitle.visibility = View.INVISIBLE
        } else {
          // Load complete
          viewPager.postWith {
            pagerAdapter.notifyDataSetChanged()
            aboutTitle.text = pagerAdapter.getPageTitle(it.currentItem)
          }
        }
      }
    }

    // Latch will show spinner if needed
    binding.progressSpinner.visibility = View.INVISIBLE

    setupViewPager(savedInstanceState)
    setupArrows()

    presenter.bind(viewLifecycle, this)
  }

  override fun onLicenseLoaded(model: AboutLibrariesModel) {
    binding.viewPager.postWith {
      if (view != null) {
        pagerAdapter.add(model)
      }
    }
  }

  override fun onAllLoaded() {
    refreshLatch.refreshing = false
  }

  private fun setupViewPager(savedInstanceState: Bundle?) {
    pagerAdapter = AboutPagerAdapter(this)
    binding.apply {
      viewPager.adapter = pagerAdapter
      viewPager.offscreenPageLimit = 1
    }

    // We must observe the data set for when it finishes.
    // There is a race condition that can cause the UI to crash if pager.setCurrentItem is
    // called before notifyDataSetChanged finishes
    pagerAdapter.registerDataSetObserver(object : DataSetObserver() {

      override fun onChanged() {
        super.onChanged()
        Timber.d("Data set changed! Set current item and unregister self")
        pagerAdapter.unregisterDataSetObserver(this)

        // Hide spinner now that loading is done
        binding.apply {
          progressSpinner.visibility = View.GONE
          viewPager.visibility = View.VISIBLE
          aboutTitle.visibility = View.VISIBLE

          // Reload the last looked at page
          if (savedInstanceState != null) {
            viewPager.setCurrentItem(savedInstanceState.getInt(KEY_PAGE, 0), false)
          }
        }
      }
    })

    val obj = object : OnPageChangeListener {
      override fun onPageScrollStateChanged(state: Int) {
      }

      override fun onPageScrolled(
        position: Int,
        positionOffset: Float,
        positionOffsetPixels: Int
      ) {
      }

      override fun onPageSelected(position: Int) {
        binding.aboutTitle.text = pagerAdapter.getPageTitle(position)
      }
    }

    listener = obj
    binding.viewPager.addOnPageChangeListener(obj)

    refreshLatch.refreshing = true
  }

  private fun setupArrows() {
    imageLoader.fromResource(R.drawable.ic_arrow_down_24dp)
        .into(binding.arrowLeft)
        .bind(viewLifecycle)
    binding.apply {
      arrowLeft.rotation = 90F
      arrowLeft.setOnDebouncedClickListener {
        viewPager.arrowScroll(View.FOCUS_LEFT)
      }
    }

    imageLoader.fromResource(R.drawable.ic_arrow_down_24dp)
        .into(binding.arrowRight)
        .bind(viewLifecycle)
    binding.apply {
      arrowRight.rotation = -90F
      arrowRight.setOnDebouncedClickListener {
        viewPager.arrowScroll(View.FOCUS_RIGHT)
      }
    }
  }

  override fun onResume() {
    super.onResume()
    toolbarActivity.withToolbar {
      it.title = "Open Source Licenses"
      it.setUpEnabled(true)
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    binding.apply {
      viewPager.removeOnPageChangeListener(listener)
      viewPager.adapter = null
      pagerAdapter.clear()

      arrowLeft.setOnDebouncedClickListener(null)
      arrowRight.setOnDebouncedClickListener(null)
    }
  }

  override fun onSaveInstanceState(outState: Bundle) {
    outState.putInt(KEY_PAGE, binding.viewPager.currentItem)
    super.onSaveInstanceState(outState)
  }

  companion object {

    const val TAG = "AboutLibrariesFragment"
    private const val KEY_PAGE = "key_current_page"

    @JvmStatic
    fun show(
      activity: FragmentActivity,
      currentFragment: Fragment, @IdRes rootViewContainer: Int
    ) {
      val fragmentManager = activity.supportFragmentManager
      if (fragmentManager.findFragmentByTag(TAG) == null) {
        fragmentManager.beginTransaction()
            .detach(currentFragment)
            .add(rootViewContainer, AboutLibrariesFragment(), TAG)
            .addToBackStack(null)
            .commit()
      }
    }
  }
}
