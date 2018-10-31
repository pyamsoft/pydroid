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
import com.pyamsoft.pydroid.bootstrap.about.AboutLibrariesViewModel
import com.pyamsoft.pydroid.bootstrap.libraries.OssLibraries
import com.pyamsoft.pydroid.bootstrap.libraries.OssLibrary
import com.pyamsoft.pydroid.loader.ImageLoader
import com.pyamsoft.pydroid.ui.PYDroid
import com.pyamsoft.pydroid.ui.app.fragment.ToolbarFragment
import com.pyamsoft.pydroid.ui.app.fragment.requireArguments
import com.pyamsoft.pydroid.ui.app.fragment.requireToolbarActivity
import com.pyamsoft.pydroid.ui.app.fragment.requireView
import com.pyamsoft.pydroid.ui.app.fragment.toolbarActivity
import com.pyamsoft.pydroid.ui.databinding.FragmentAboutLibrariesBinding
import com.pyamsoft.pydroid.ui.util.Snackbreak
import com.pyamsoft.pydroid.ui.util.commit
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

    requireArguments().also {
      backStackCount = it.getInt(KEY_BACK_STACK, 0)
    }


    refreshLatch = RefreshLatch.create(viewLifecycleOwner, delay = 150L) {
      binding.apply {
        if (it) {
          progressSpinner.visibility = View.VISIBLE
          aboutList.visibility = View.INVISIBLE
        } else {
          // Load complete
          progressSpinner.visibility = View.GONE
          aboutList.visibility = View.VISIBLE

          val lastViewed = lastViewedItem
          aboutList.scrollToPosition(lastViewed)
        }
      }
    }

    lastViewedItem = savedInstanceState?.getInt(KEY_PAGE) ?: 0
    setupAboutList()

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

  private fun onLicenseLoaded(licenses: List<OssLibrary>) {
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
    pagerAdapter = AboutPagerAdapter(requireActivity())
    binding.apply {
      aboutList.adapter = pagerAdapter
      aboutList.layoutManager = LinearLayoutManager(requireActivity()).apply {
        initialPrefetchItemCount = 3
        isItemPrefetchEnabled = false
      }
    }
    refreshLatch.isRefreshing = true
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
    fun show(
      activity: FragmentActivity,
      @IdRes rootViewContainer: Int
    ) {
      // If you're using this function, all of these are available
      OssLibraries.CORE = true
      OssLibraries.BOOTSTRAP = true
      OssLibraries.UTIL = true
      OssLibraries.LOADER = true
      OssLibraries.UI = true

      val fragmentManager = activity.supportFragmentManager
      val backStackCount = fragmentManager.backStackEntryCount
      if (fragmentManager.findFragmentByTag(TAG) == null) {
        fragmentManager.beginTransaction()
            .replace(rootViewContainer, newInstance(backStackCount), TAG)
            .addToBackStack(null)
            .commit(activity)
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
