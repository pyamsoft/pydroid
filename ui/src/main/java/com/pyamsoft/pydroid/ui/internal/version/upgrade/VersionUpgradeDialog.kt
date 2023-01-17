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

package com.pyamsoft.pydroid.ui.internal.version.upgrade

import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.pyamsoft.pydroid.core.Logger
import com.pyamsoft.pydroid.theme.keylines
import com.pyamsoft.pydroid.ui.inject.ComposableInjector
import com.pyamsoft.pydroid.ui.inject.rememberComposableInjector
import com.pyamsoft.pydroid.ui.internal.pydroid.ObjectGraph
import com.pyamsoft.pydroid.ui.util.rememberActivity
import com.pyamsoft.pydroid.ui.util.rememberNotNull

internal class VersionUpgradeDialogInjector : ComposableInjector() {

  internal var viewModel: VersionUpgradeViewModeler? = null

  override fun onInject(activity: FragmentActivity) {
    ObjectGraph.ActivityScope.retrieve(activity)
        .injector()
        .plusVersionUpgrade()
        .create()
        .inject(this)
  }

  override fun onDispose() {
    viewModel = null
  }
}

@Composable
internal fun VersionUpgradeDialog(
    modifier: Modifier = Modifier,
    newVersionCode: Int,
    onDismiss: () -> Unit,
) {
  val component = rememberComposableInjector { VersionUpgradeDialogInjector() }

  val viewModel = rememberNotNull(component.viewModel)

  val activity = rememberActivity()
  val handleCompleteUpgrade by rememberUpdatedState {
    viewModel.completeUpgrade(
        // Don't use scope since if this leaves Composition it would die
        scope = activity.lifecycleScope,
        onUpgradeComplete = {
          Logger.d("Upgrade complete, dismiss")
          activity.finish()
        },
    )
  }

  Dialog(
      onDismissRequest = onDismiss,
  ) {
    VersionUpgradeScreen(
        modifier = modifier.padding(MaterialTheme.keylines.content),
        state = viewModel.state(),
        newVersionCode = newVersionCode,
        onUpgrade = handleCompleteUpgrade,
        onClose = onDismiss,
    )
  }
}
