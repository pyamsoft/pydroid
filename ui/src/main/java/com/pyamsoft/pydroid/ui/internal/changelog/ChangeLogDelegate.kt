/*
 * Copyright 2021 Peter Kenji Yamanaka
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

import androidx.lifecycle.lifecycleScope
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.ui.app.PYDroidActivity
import com.pyamsoft.pydroid.ui.internal.changelog.dialog.ChangeLogDialog
import com.pyamsoft.pydroid.util.doOnDestroy

internal class ChangeLogDelegate(activity: PYDroidActivity, viewModel: ChangeLogViewModeler) {

  private var activity: PYDroidActivity? = activity
  private var viewModel: ChangeLogViewModeler? = viewModel

  /** Bind Activity for related ChangeLog events */
  fun bindEvents() {
    activity.requireNotNull().doOnDestroy {
      viewModel = null
      activity = null
    }
  }

  /** Show changelog */
  fun showChangeLog() {
    val act = activity.requireNotNull()
    viewModel.requireNotNull().handleShow(
            scope = act.lifecycleScope,
            force = false,
        ) { ChangeLogDialog.open(act) }
  }
}
