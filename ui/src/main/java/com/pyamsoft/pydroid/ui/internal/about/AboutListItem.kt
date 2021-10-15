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

package com.pyamsoft.pydroid.ui.internal.about

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pyamsoft.pydroid.bootstrap.libraries.OssLibraries
import com.pyamsoft.pydroid.bootstrap.libraries.OssLibrary
import com.pyamsoft.pydroid.ui.R

@Composable
internal fun AboutListItem(
    library: OssLibrary,
    onViewHomePage: () -> Unit,
    onViewLicense: () -> Unit
) {
  Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(size = 4.dp),
      elevation = 2.dp,
  ) {
    Column(
        modifier = Modifier.padding(8.dp),
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

      Row(modifier = Modifier.padding(top = 8.dp)) {
        ViewLicense(
            onClick = onViewLicense,
        )
        VisitHomepage(
            onClick = onViewHomePage,
        )
      }
    }
  }
}

@Composable
private fun Name(library: OssLibrary) {
  Text(
      style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold),
      text = library.name,
  )
}

@Composable
private fun License(library: OssLibrary) {
  Text(
      style = MaterialTheme.typography.caption,
      text = stringResource(R.string.license_name, library.licenseName),
  )
}

@Composable
@OptIn(ExperimentalAnimationApi::class)
private fun Description(library: OssLibrary) {
  val description = library.description

  AnimatedVisibility(visible = description.isNotBlank()) {
    Box(
        modifier = Modifier.padding(vertical = 8.dp),
    ) {
      Text(
          style = MaterialTheme.typography.body2,
          text = description,
      )
    }
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
  Box(modifier = Modifier.padding(start = 8.dp)) {
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
  Surface {
    AboutListItem(
        library = OssLibraries.libraries().first(),
        onViewLicense = {},
        onViewHomePage = {},
    )
  }
}
