/*
 * Copyright 2024 pyamsoft
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

package com.pyamsoft.pydroid.ui.internal.datapolicy.dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.ImageLoader
import com.pyamsoft.pydroid.theme.keylines
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.haptics.LocalHapticManager
import com.pyamsoft.pydroid.ui.internal.app.AppHeader
import com.pyamsoft.pydroid.ui.internal.test.createNewTestImageLoader

private val MAX_HEIGHT_PORTRAIT = 360.dp

private enum class DataPolicyDisclosureScreenItems {
  DISCLOSURE,
  LINKS,
  ERROR,
  ACTIONS,
}

@Composable
internal fun DataPolicyDisclosureScreen(
    modifier: Modifier = Modifier,
    state: DataPolicyDialogViewState,
    imageLoader: ImageLoader,
    onPrivacyPolicyClicked: () -> Unit,
    onTermsOfServiceClicked: () -> Unit,
    onNavigationErrorDismissed: () -> Unit,
    onAccept: () -> Unit,
    onReject: () -> Unit,
) {
  val snackbarHostState = remember { SnackbarHostState() }
  val icon by state.icon.collectAsStateWithLifecycle()
  val name by state.name.collectAsStateWithLifecycle()
  val navigationError by state.navigationError.collectAsStateWithLifecycle()

  AppHeader(
      modifier = modifier,
      icon = icon,
      name = name,
      imageLoader = imageLoader,
  ) {
    item(
        contentType = DataPolicyDisclosureScreenItems.DISCLOSURE,
    ) {
      Disclosure(
          modifier = Modifier.fillMaxWidth().heightIn(max = MAX_HEIGHT_PORTRAIT),
          name = name,
      )
    }

    item(
        contentType = DataPolicyDisclosureScreenItems.LINKS,
    ) {
      Links(
          modifier = Modifier.fillMaxWidth(),
          onPrivacyPolicyClicked = onPrivacyPolicyClicked,
          onTermsOfServiceClicked = onTermsOfServiceClicked,
      )
    }

    item(
        contentType = DataPolicyDisclosureScreenItems.ERROR,
    ) {
      NavigationError(
          modifier = Modifier.fillMaxWidth(),
          snackbarHostState = snackbarHostState,
          error = navigationError,
          onSnackbarDismissed = onNavigationErrorDismissed,
      )
    }

    item(
        contentType = DataPolicyDisclosureScreenItems.ACTIONS,
    ) {
      Actions(
          modifier = Modifier.fillMaxWidth(),
          onAccept = onAccept,
          onReject = onReject,
      )
    }
  }
}

@Composable
private fun Links(
    modifier: Modifier = Modifier,
    onPrivacyPolicyClicked: () -> Unit,
    onTermsOfServiceClicked: () -> Unit,
) {

  Column(
      modifier = modifier.padding(vertical = MaterialTheme.keylines.baseline),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
  ) {
    Text(
        modifier = Modifier.clickable { onTermsOfServiceClicked() },
        text = stringResource(R.string.terms_conditions),
        style =
            MaterialTheme.typography.labelSmall.copy(
                color = MaterialTheme.colorScheme.primary,
            ),
    )
    Text(
        modifier =
            Modifier.clickable { onPrivacyPolicyClicked() }
                .padding(top = MaterialTheme.keylines.baseline),
        text = stringResource(R.string.privacy_policy),
        style =
            MaterialTheme.typography.labelSmall.copy(
                color = MaterialTheme.colorScheme.primary,
            ),
    )
  }
}

@Composable
private fun Disclosure(
    modifier: Modifier = Modifier,
    name: String,
) {
  val disclosureStyle =
      MaterialTheme.typography.bodyMedium.copy(
          color = MaterialTheme.colorScheme.onSurfaceVariant,
      )

  Column(
      modifier = modifier.padding(MaterialTheme.keylines.content),
  ) {
    Text(
        text = stringResource(R.string.disclosure_title, name),
        style = MaterialTheme.typography.bodyLarge,
    )

    Text(
        modifier = Modifier.padding(top = MaterialTheme.keylines.content),
        text = stringResource(R.string.disclosure_start).trimIndent().replace("\n", " "),
        style = disclosureStyle,
    )

    Text(
        modifier = Modifier.padding(top = MaterialTheme.keylines.content),
        text = stringResource(R.string.disclosure_end, name).trimIndent().replace("\n", " "),
        style = disclosureStyle,
    )
  }
}

@Composable
private fun Actions(
    modifier: Modifier = Modifier,
    onAccept: () -> Unit,
    onReject: () -> Unit,
) {
  val hapticManager = LocalHapticManager.current

  Column(
      modifier =
          modifier
              .padding(horizontal = MaterialTheme.keylines.content)
              .padding(top = MaterialTheme.keylines.content),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
  ) {
    Button(
        onClick = {
          hapticManager?.confirmButtonPress()
          onAccept()
        },
    ) {
      Text(
          text = stringResource(R.string.dpd_accept),
      )
    }
    TextButton(
        modifier = Modifier.padding(top = MaterialTheme.keylines.baseline),
        onClick = {
          hapticManager?.cancelButtonPress()
          onReject()
        },
    ) {
      Text(
          text = stringResource(R.string.dpd_reject),
          fontSize = 12.sp,
      )
    }
  }
}

@Composable
private fun NavigationError(
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState,
    error: Throwable?,
    onSnackbarDismissed: () -> Unit,
) {
  SnackbarHost(
      modifier = modifier,
      hostState = snackbarHostState,
  )

  if (error != null) {
    LaunchedEffect(error) {
      snackbarHostState.showSnackbar(
          message = error.message ?: "An unexpected error occurred",
          duration = SnackbarDuration.Long,
      )

      // We ignore the showSnackbar result because we don't care (no actions)
      onSnackbarDismissed()
    }
  }
}

@Preview
@Composable
private fun PreviewDataPolicyDisclosureScreen() {
  DataPolicyDisclosureScreen(
      state =
          MutableDataPolicyDialogViewState().apply {
            icon.value = 0
            name.value = "TEST"
            navigationError.value = null
          },
      imageLoader = createNewTestImageLoader(),
      onPrivacyPolicyClicked = {},
      onTermsOfServiceClicked = {},
      onNavigationErrorDismissed = {},
      onAccept = {},
      onReject = {},
  )
}
