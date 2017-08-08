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

import android.support.annotation.RestrictTo
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import com.pyamsoft.pydroid.about.AboutLibrariesModel
import timber.log.Timber

@RestrictTo(RestrictTo.Scope.LIBRARY) internal class AboutPagerAdapter internal constructor(
    fm: Fragment) : FragmentStatePagerAdapter(fm.childFragmentManager) {

  private val models: MutableList<AboutLibrariesModel> = ArrayList()
  private var newModelAdded: Boolean = false

  fun add(model: AboutLibrariesModel) {
    if (model in models) {
      Timber.d("AboutPagerAdapter already has %s entry", model)
    } else {
      models.add(model)
      newModelAdded = true
    }
  }

  fun clear() {
    models.clear()
    newModelAdded = true
  }

  override fun notifyDataSetChanged() {
    if (newModelAdded) {
      newModelAdded = false
      super.notifyDataSetChanged()
    } else {
      Timber.d("No changes to adapter, do not modify")
    }
  }

  override fun getCount(): Int {
    return models.size
  }

  override fun getItem(position: Int): Fragment {
    return AboutPagerFragment.newInstance(models[position])
  }

  override fun getPageTitle(position: Int): CharSequence {
    return models[position].name
  }
}

