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
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter
import com.pyamsoft.pydroid.about.AboutLibrariesModel
import com.pyamsoft.pydroid.about.AboutLibrariesPresenter
import com.pyamsoft.pydroid.about.AboutLibrariesPresenter.LoadCallback
import com.pyamsoft.pydroid.ui.PYDroid
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.about.AboutLibrariesFragment.BackStackState.LAST
import com.pyamsoft.pydroid.ui.about.AboutLibrariesFragment.BackStackState.NOT_LAST
import com.pyamsoft.pydroid.ui.app.fragment.ActionBarFragment
import kotlinx.android.synthetic.main.fragment_about_libraries.about_loading
import kotlinx.android.synthetic.main.fragment_about_libraries.recycler_about_libraries
import timber.log.Timber

class AboutLibrariesFragment : ActionBarFragment() {

  internal lateinit var fastItemAdapter: FastItemAdapter<AboutLibrariesItem>
  internal lateinit var presenter: AboutLibrariesPresenter
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
    fastItemAdapter = FastItemAdapter<AboutLibrariesItem>()
    return inflater?.inflate(R.layout.fragment_about_libraries, container, false)
  }

  override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    fastItemAdapter.withSelectable(true)

    val manager = LinearLayoutManager(context)
    manager.isItemPrefetchEnabled = true
    manager.initialPrefetchItemCount = 3
    recycler_about_libraries.layoutManager = manager
    recycler_about_libraries.clipToPadding = false
    recycler_about_libraries.setHasFixedSize(true)
    recycler_about_libraries.adapter = fastItemAdapter
  }

  override fun onStart() {
    super.onStart()
    presenter.loadLicenses(object : LoadCallback {
      override fun onLicenseLoaded(model: AboutLibrariesModel) {
        var alreadyHas = false
        val items = fastItemAdapter.adapterItems
        for (item in items) {
          if (item.model === model) {
            alreadyHas = true
            break
          }
        }

        if (!alreadyHas) {
          if (items.isEmpty()) {
            Timber.d("Adding first Library item, hide loading spinner")
            about_loading.visibility = View.GONE
            recycler_about_libraries.visibility = View.VISIBLE
          }

          fastItemAdapter.add(AboutLibrariesItem(model))
        }

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

