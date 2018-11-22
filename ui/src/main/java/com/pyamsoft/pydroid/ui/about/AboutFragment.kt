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
import com.pyamsoft.pydroid.bootstrap.about.AboutViewModel
import com.pyamsoft.pydroid.bootstrap.libraries.OssLibraries
import com.pyamsoft.pydroid.bootstrap.libraries.OssLibrary
import com.pyamsoft.pydroid.core.singleDisposable
import com.pyamsoft.pydroid.core.tryDispose
import com.pyamsoft.pydroid.ui.PYDroid
import com.pyamsoft.pydroid.ui.app.fragment.ToolbarFragment
import com.pyamsoft.pydroid.ui.app.fragment.requireArguments
import com.pyamsoft.pydroid.ui.app.fragment.requireToolbarActivity
import com.pyamsoft.pydroid.ui.app.fragment.toolbarActivity
import com.pyamsoft.pydroid.ui.util.commit
import com.pyamsoft.pydroid.ui.util.setUpEnabled

class AboutFragment : ToolbarFragment() {

  internal lateinit var rootView: AboutView
  internal lateinit var viewModel: AboutViewModel

  private var backStackCount: Int = 0
  private var oldTitle: CharSequence? = null

  private var loadLicensesDisposable by singleDisposable()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    requireArguments().also {
      backStackCount = it.getInt(KEY_BACK_STACK, 0)
    }
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    PYDroid.obtain(requireContext())
        .plusAboutComponent(
            viewLifecycleOwner, requireActivity(), inflater, container, savedInstanceState
        )
        .inject(this)

    return rootView.root()
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)
    loadLicensesDisposable = viewModel.loadLicenses(false,
        onLoadBegin = { forced: Boolean -> rootView.onLoadBegin(forced) },
        onLoadSuccess = { licenses: List<OssLibrary> -> rootView.onLoadSuccess(licenses) },
        onLoadError = { error: Throwable -> rootView.onLoadError(error) },
        onLoadComplete = { rootView.onLoadComplete() }
    )
  }

  override fun onDestroyView() {
    super.onDestroyView()
    loadLicensesDisposable.tryDispose()
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

  override fun onSaveInstanceState(outState: Bundle) {
    rootView.saveInstanceState(outState)
    super.onSaveInstanceState(outState)
  }

  companion object {

    private const val TAG = "AboutFragment"
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
    private fun newInstance(backStackCount: Int): AboutFragment {
      return AboutFragment().apply {
        arguments = Bundle().apply {
          putInt(KEY_BACK_STACK, backStackCount)
        }
      }
    }
  }
}
