/*
 * Copyright 2026 pyamsoft
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pyamsoft.pydroid.ui.internal.settings.newstuff

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pyamsoft.pydroid.theme.keylines
import com.pyamsoft.pydroid.ui.internal.settings.SettingsAppViewState

internal fun LazyListScope.renderAppSettings(
  modifier: Modifier = Modifier,
  state: SettingsAppViewState,
) {
  item {
    Card(
      modifier = modifier.padding(top = MaterialTheme.keylines.content),
      border =
        BorderStroke(
          width = 2.dp,
          color = MaterialTheme.colorScheme.primaryContainer,
        ),
      shape = MaterialTheme.shapes.large,
    ) {

    }
  }
}
