/*
 * Copyright 2022 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.ui.internal.changelog

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.ui.internal.changelog.dialog.ChangeLogDialog
import com.pyamsoft.pydroid.util.doOnCreate
import com.pyamsoft.pydroid.util.doOnDestroy

/** Handles ChangeLog related work in an Activity */
internal class ChangeLogDelegate(
    activity: FragmentActivity,
    viewModel: ChangeLogViewModeler,
) {

  private var activity: FragmentActivity? = activity
  private var viewModel: ChangeLogViewModeler? = viewModel

  /** Bind Activity for related DataPolicy events */
  fun bindEvents() {
    val a = activity.requireNotNull()
    a.doOnDestroy {
      viewModel = null
      activity = null
    }

    a.doOnCreate {
      viewModel
          .requireNotNull()
          .bind(
              scope = a.lifecycleScope,
          )
    }
  }

  /** Show Change Log */
  fun showChangeLog() {
    val act = activity.requireNotNull()
    viewModel
        .requireNotNull()
        .handleShow(
            scope = act.lifecycleScope,
            force = false,
            onShowChangeLog = { ChangeLogDialog.show(act) },
        )
  }
}
