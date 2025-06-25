/*
 * Copyright 2025 pyamsoft
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

package com.pyamsoft.pydroid.ui.internal.changelog.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pyamsoft.pydroid.theme.keylines
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.internal.changelog.ChangeLogLine
import com.pyamsoft.pydroid.ui.internal.changelog.ChangeLogLine.Type.BUGFIX
import com.pyamsoft.pydroid.ui.internal.changelog.ChangeLogLine.Type.CHANGE
import com.pyamsoft.pydroid.ui.internal.changelog.ChangeLogLine.Type.FEATURE

private val TYPE_WIDTH = 80.dp

@Composable
internal fun ChangeLogListItem(
    modifier: Modifier = Modifier,
    line: ChangeLogLine,
) {
  Row(
      modifier = modifier,
      verticalAlignment = Alignment.CenterVertically,
  ) {
    Column(
        modifier = Modifier.width(TYPE_WIDTH),
        verticalArrangement = Arrangement.Center,
    ) {
      Type(
          line = line,
      )
    }

    Column(
        modifier = Modifier.padding(MaterialTheme.keylines.baseline).weight(1F),
    ) {
      Line(
          line = line,
      )
    }
  }
}

@Composable
private fun Type(line: ChangeLogLine) {
  val stringResourceId =
      remember(line) {
        when (line.type) {
          BUGFIX -> R.string.changelog_bugfix
          CHANGE -> R.string.changelog_change
          FEATURE -> R.string.changelog_feature
        }
      }
  val text = stringResource(stringResourceId)

  Text(
      text = text,
      style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.W700),
  )
}

@Composable
private fun Line(line: ChangeLogLine) {
  Text(
      text = line.line,
      style = MaterialTheme.typography.bodyLarge,
  )
}

@Preview
@Composable
private fun PreviewChangeLogListItem() {
  Surface {
    ChangeLogListItem(
        line =
            ChangeLogLine(
                ChangeLogLine.Type.FEATURE,
                "Changed Stuff, like a lot of stuff, so much stuff that you wouldn't even recognize",
            ),
    )
  }
}
