/*
 * Copyright 2020 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.ui.util

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.pyamsoft.pydroid.util.doOnResume
import timber.log.Timber

/**
 * Using the fragment manager to handle transactions, this guarantees that any old versions of the
 * dialog fragment are removed before a new one is added.
 *
 * Because of doOnResume this also guarantees that any children fragments are also ready
 */
public fun DialogFragment.show(activity: FragmentActivity, tag: String) {
  require(tag.isNotBlank()) { "Cannot use blank tag" }

  activity.doOnResume {
    val fragmentManager = activity.supportFragmentManager
    val transaction = fragmentManager.beginTransaction()
    val prev = fragmentManager.findFragmentByTag(tag)
    if (prev != null) {
      Timber.d("Remove old fragment with tag: $tag")
      transaction.remove(prev)
    }

    Timber.d("Add new fragment with tag: $tag")
    show(transaction, tag)
  }
}
