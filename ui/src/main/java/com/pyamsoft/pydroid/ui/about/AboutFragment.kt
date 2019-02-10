/*
 * Copyright 2019 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.ui.about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CheckResult
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.pyamsoft.pydroid.bootstrap.libraries.OssLibraries
import com.pyamsoft.pydroid.bootstrap.libraries.OssLibrary
import com.pyamsoft.pydroid.ui.PYDroid
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.about.dialog.ViewUrlDialog
import com.pyamsoft.pydroid.ui.about.listitem.AboutItemPresenter
import com.pyamsoft.pydroid.ui.app.requireArguments
import com.pyamsoft.pydroid.ui.app.requireToolbarActivity
import com.pyamsoft.pydroid.ui.app.toolbarActivity
import com.pyamsoft.pydroid.ui.util.commit
import com.pyamsoft.pydroid.ui.util.setUpEnabled
import com.pyamsoft.pydroid.ui.util.show
import com.pyamsoft.pydroid.ui.widget.spinner.SpinnerView

class AboutFragment : Fragment(), AboutPresenter.Callback, AboutItemPresenter.Callback {

  internal lateinit var listView: AboutListView
  internal lateinit var spinner: SpinnerView
  internal lateinit var presenter: AboutPresenter

  private var backStackCount: Int = 0
  private var oldTitle: CharSequence? = null

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
    val root = inflater.inflate(R.layout.layout_frame, container, false)

    PYDroid.obtain(root.context.applicationContext)
        .plusAboutComponent(viewLifecycleOwner, root as ViewGroup)
        .inject(this)

    return root
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)
    listView.inflate(savedInstanceState)
    spinner.inflate(savedInstanceState)
    presenter.bind(this)
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    listView.saveState(outState)
    spinner.saveState(outState)
  }

  override fun onDestroyView() {
    super.onDestroyView()
    listView.teardown()
    spinner.teardown()
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

  override fun onLicenseLoadBegin() {
    listView.hide()
    spinner.show()
  }

  override fun onLicensesLoaded(licenses: List<OssLibrary>) {
    listView.loadLicenses(licenses)
  }

  override fun onLicenseLoadError(throwable: Throwable) {
    listView.showError(throwable)
  }

  override fun onLicenseLoadComplete() {
    spinner.hide()
    listView.show()
  }

  override fun onViewLicense(
    name: String,
    licenseUrl: String
  ) {
    ViewUrlDialog.newInstance(name, link = licenseUrl)
        .show(requireActivity(), ViewUrlDialog.TAG)
  }

  override fun onVisitHomepage(
    name: String,
    homepageUrl: String
  ) {
    ViewUrlDialog.newInstance(name, link = homepageUrl)
        .show(requireActivity(), ViewUrlDialog.TAG)
  }

  companion object {

    private const val TAG = "AboutFragment"
    private const val KEY_BACK_STACK = "key_back_stack"

    @JvmStatic
    fun show(
      activity: FragmentActivity,
      @IdRes container: Int
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
            .replace(container, newInstance(backStackCount), TAG)
            .addToBackStack(null)
            .commit(activity)
      }
    }

    @Suppress("unused")
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