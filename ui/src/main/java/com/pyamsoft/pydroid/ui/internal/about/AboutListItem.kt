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

package com.pyamsoft.pydroid.ui.internal.about

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.pyamsoft.pydroid.bootstrap.libraries.OssLibraries
import com.pyamsoft.pydroid.bootstrap.libraries.OssLibrary
import com.pyamsoft.pydroid.theme.keylines
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.defaults.TypographyDefaults
import com.pyamsoft.pydroid.ui.haptics.LocalHapticManager

@Composable
internal fun AboutListItem(
    modifier: Modifier = Modifier,
    library: OssLibrary,
    onViewHomePage: (OssLibrary) -> Unit,
    onViewLicense: (OssLibrary) -> Unit,
) {
  val hapticManager = LocalHapticManager.current

  Card(
      modifier = modifier,
      shape = MaterialTheme.shapes.medium,
      elevation = CardDefaults.cardElevation(),
  ) {
    Column(
        modifier = Modifier.padding(MaterialTheme.keylines.baseline),
    ) {
      Name(
          library = library,
      )
      License(
          library = library,
      )
      Description(
          library = library,
      )

      Row(
          modifier = Modifier.padding(top = MaterialTheme.keylines.baseline),
          verticalAlignment = Alignment.CenterVertically,
      ) {
        ViewLicense(
            onClick = {
              hapticManager?.confirmButtonPress()
              onViewLicense(library)
            },
        )
        VisitHomepage(
            onClick = {
              hapticManager?.confirmButtonPress()
              onViewHomePage(library)
            },
        )
      }
    }
  }
}

@Composable
private fun Name(library: OssLibrary) {
  Text(
      style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.W700),
      text = library.name,
  )
}

@Composable
private fun License(library: OssLibrary) {
  Text(
      style =
          MaterialTheme.typography.labelSmall.copy(
              color =
                  MaterialTheme.colorScheme.onSurfaceVariant.copy(
                      alpha = TypographyDefaults.ALPHA_DISABLED,
                  ),
          ),
      text = stringResource(R.string.license_name, library.licenseName),
  )
}

@Composable
private fun Description(library: OssLibrary) {
  val description = library.description

  if (description.isNotBlank()) {
    Text(
        modifier = Modifier.padding(vertical = MaterialTheme.keylines.baseline),
        style = MaterialTheme.typography.bodyMedium,
        text = description,
    )
  }
}

@Composable
private fun ViewLicense(onClick: () -> Unit) {
  TextButton(
      onClick = onClick,
  ) {
    Text(
        text = stringResource(R.string.view_license),
    )
  }
}

@Composable
private fun VisitHomepage(onClick: () -> Unit) {
  Box(
      modifier = Modifier.padding(start = MaterialTheme.keylines.baseline),
  ) {
    TextButton(
        onClick = onClick,
    ) {
      Text(
          text = stringResource(R.string.visit_homepage),
      )
    }
  }
}

@Preview
@Composable
private fun PreviewAboutListItem() {
  val context = LocalContext.current
  Surface {
    AboutListItem(
        library = OssLibraries.libraries(context).first(),
        onViewLicense = {},
        onViewHomePage = {},
    )
  }
}
