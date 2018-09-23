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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CheckResult
import androidx.annotation.IdRes
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.pyamsoft.pydroid.bootstrap.about.AboutLibrariesModel
import com.pyamsoft.pydroid.bootstrap.about.AboutLibrariesViewModel
import com.pyamsoft.pydroid.loader.ImageLoader
import com.pyamsoft.pydroid.ui.PYDroid
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.app.fragment.ToolbarFragment
import com.pyamsoft.pydroid.ui.app.fragment.requireToolbarActivity
import com.pyamsoft.pydroid.ui.app.fragment.requireView
import com.pyamsoft.pydroid.ui.app.fragment.toolbarActivity
import com.pyamsoft.pydroid.ui.databinding.FragmentAboutLibrariesBinding
import com.pyamsoft.pydroid.ui.util.Snackbreak
import com.pyamsoft.pydroid.ui.util.setOnDebouncedClickListener
import com.pyamsoft.pydroid.ui.util.setUpEnabled
import com.pyamsoft.pydroid.ui.widget.RefreshLatch

class AboutLibrariesFragment : ToolbarFragment() {

  internal lateinit var viewModel: AboutLibrariesViewModel
  internal lateinit var imageLoader: ImageLoader
  private lateinit var pagerAdapter: AboutPagerAdapter
  private lateinit var binding: FragmentAboutLibrariesBinding
  private lateinit var refreshLatch: RefreshLatch
  private var lastViewedItem: Int = 0
  private var backStackCount: Int = 0
  private var oldTitle: CharSequence? = null

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    binding = FragmentAboutLibrariesBinding.inflate(inflater, container, false)

    PYDroid.obtain(requireContext())
        .plusAboutComponent(viewLifecycleOwner)
        .inject(this)

    arguments?.also {
      backStackCount = it.getInt(KEY_BACK_STACK, 0)
    }


    refreshLatch = RefreshLatch.create(viewLifecycleOwner, delay = 150L) {
      binding.apply {
        if (it) {
          progressSpinner.visibility = View.VISIBLE
          aboutList.visibility = View.INVISIBLE
          aboutTitle.visibility = View.INVISIBLE
        } else {
          // Load complete
          progressSpinner.visibility = View.GONE
          aboutList.visibility = View.VISIBLE
          aboutTitle.visibility = View.VISIBLE

          val lastViewed = lastViewedItem
          aboutList.scrollToPosition(lastViewed)
          aboutTitle.text = pagerAdapter.getTitleAt(lastViewed)
        }
      }
    }

    // Latch will show spinner if needed
    binding.progressSpinner.visibility = View.INVISIBLE

    lastViewedItem = savedInstanceState?.getInt(KEY_PAGE) ?: 0
    setupAboutList()
    setupArrows()

    observeLicenses()
    viewModel.loadLicenses(false)

    return binding.root
  }

  private fun observeLicenses() {
    viewModel.onLicensesLoaded { wrapper ->
      wrapper.onLoading { onLicenseLoadBegin() }
      wrapper.onSuccess { onLicenseLoaded(it) }
      wrapper.onError { onLicenseLoadError(it) }
      wrapper.onComplete { onLicenseLoadComplete() }
    }
  }

  @CheckResult
  private fun getCurrentPosition(): Int {
    val manager = binding.aboutList.layoutManager
    if (manager is LinearLayoutManager) {
      return manager.findFirstVisibleItemPosition()
    } else {
      return 0
    }
  }

  private fun onLicenseLoadBegin() {
    refreshLatch.isRefreshing = true
  }

  private fun onLicenseLoaded(licenses: List<AboutLibrariesModel>) {
    pagerAdapter.addAll(licenses)
  }

  private fun onLicenseLoadError(throwable: Throwable) {
    Snackbreak.short(requireView(), throwable.localizedMessage)
        .show()
  }

  private fun onLicenseLoadComplete() {
    refreshLatch.isRefreshing = false
  }

  private fun setupAboutList() {
    pagerAdapter = AboutPagerAdapter()
    binding.apply {
      aboutList.adapter = pagerAdapter
      aboutList.layoutManager =
          LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false).apply {
            initialPrefetchItemCount = 0
            isItemPrefetchEnabled = false
          }

      val snapHelper = PagerSnapHelper()
      snapHelper.attachToRecyclerView(aboutList)

      aboutList.addOnScrollListener(object : RecyclerView.OnScrollListener() {

        override fun onScrollStateChanged(
          recyclerView: RecyclerView,
          newState: Int
        ) {
          super.onScrollStateChanged(recyclerView, newState)
          if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            aboutTitle.text = pagerAdapter.getTitleAt(getCurrentPosition())
          }
        }

      })
    }
    refreshLatch.isRefreshing = true
  }

  private fun setupArrows() {
    imageLoader.load(R.drawable.ic_arrow_down_24dp)
        .into(binding.arrowLeft)
        .bind(viewLifecycleOwner)
    binding.apply {
      arrowLeft.rotation = 90F
      arrowLeft.setOnDebouncedClickListener {
        val position = getCurrentPosition()
        if (position > 0) {
          aboutList.smoothScrollToPosition(position - 1)
          aboutTitle.text = pagerAdapter.getTitleAt(position - 1)
        }
      }
    }

    imageLoader.load(R.drawable.ic_arrow_down_24dp)
        .into(binding.arrowRight)
        .bind(viewLifecycleOwner)
    binding.apply {
      arrowRight.rotation = -90F
      arrowRight.setOnDebouncedClickListener {
        val position = getCurrentPosition()
        if (position < pagerAdapter.itemCount) {
          aboutList.smoothScrollToPosition(position + 1)
          aboutTitle.text = pagerAdapter.getTitleAt(position + 1)
        }
      }
    }
  }

  override fun onResume() {
    super.onResume()
    requireToolbarActivity().withToolbar {
      if (oldTitle == null) {
        oldTitle = it.title
      }
      it.title = "Open Source Libraries"
      it.setUpEnabled(true)
    }
  }

  override fun onPause() {
    super.onPause()
    if (isRemoving) {
      toolbarActivity?.withToolbar {
        // Set title back to original
        it.title = oldTitle ?: it.title

        // If this page was last on the back stack, set up false
        if (backStackCount == 0) {
          it.setUpEnabled(false)
        }
      }
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    binding.apply {
      aboutList.adapter = null
      aboutList.clearOnScrollListeners()
      pagerAdapter.clear()

      arrowLeft.setOnDebouncedClickListener(null)
      arrowRight.setOnDebouncedClickListener(null)

      unbind()
    }
  }

  override fun onSaveInstanceState(outState: Bundle) {
    outState.putInt(KEY_PAGE, getCurrentPosition())
    super.onSaveInstanceState(outState)
  }

  companion object {

    private const val TAG = "AboutLibrariesFragment"
    private const val KEY_PAGE = "key_current_page"
    private const val KEY_BACK_STACK = "key_back_stack"

    @JvmStatic
    fun show(activity: FragmentActivity, @IdRes rootViewContainer: Int) {
      val fragmentManager = activity.supportFragmentManager
      val backStackCount = fragmentManager.backStackEntryCount
      if (fragmentManager.findFragmentByTag(TAG) == null) {
        fragmentManager.beginTransaction()
            .replace(rootViewContainer, newInstance(backStackCount), TAG)
            .addToBackStack(null)
            .commit()
      }
    }

    @JvmStatic
    @CheckResult
    fun isPresent(activity: FragmentActivity): Boolean =
      (activity.supportFragmentManager.findFragmentByTag(TAG) != null)

    @JvmStatic
    @CheckResult
    private fun newInstance(backStackCount: Int): AboutLibrariesFragment {
      return AboutLibrariesFragment().apply {
        arguments = Bundle().apply {
          putInt(KEY_BACK_STACK, backStackCount)
        }
      }
    }
  }
}
