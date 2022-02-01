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

package com.pyamsoft.pydroid.ui.internal.datapolicy.dialog

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.defaults.DialogDefaults
import com.pyamsoft.pydroid.ui.internal.app.AppHeader
import com.pyamsoft.pydroid.ui.internal.test.createNewTestImageLoader

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

  val icon = state.icon
  val name = state.name
  val navigationError = state.navigationError

  val configuration = LocalConfiguration.current
  val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT

  Column(
      modifier = modifier,
  ) {
    AppHeader(
        modifier = Modifier.fillMaxWidth(),
        elevation = DialogDefaults.DialogElevation,
        icon = icon,
        name = name,
        imageLoader = imageLoader,
    )

    Surface(
        elevation = DialogDefaults.DialogElevation,
    ) {
      Column {
        Disclosure(
            modifier =
                Modifier.fillMaxWidth()
                    .heightIn(
                        max = if (isPortrait) 400.dp else 100.dp,
                    ),
            name = name,
        )
        Links(
            modifier = Modifier.fillMaxWidth(),
            isPortrait = isPortrait,
            onPrivacyPolicyClicked = onPrivacyPolicyClicked,
            onTermsOfServiceClicked = onTermsOfServiceClicked,
        )
        Actions(
            modifier = Modifier.fillMaxWidth(),
            isPortrait = isPortrait,
            onAccept = onAccept,
            onReject = onReject,
        )
        NavigationError(
            snackbarHostState = snackbarHostState,
            error = navigationError,
            onSnackbarDismissed = onNavigationErrorDismissed,
        )
      }
    }
  }
}

@Composable
private fun Links(
    modifier: Modifier = Modifier,
    isPortrait: Boolean,
    onPrivacyPolicyClicked: () -> Unit,
    onTermsOfServiceClicked: () -> Unit,
) {
  val content =
      @Composable
      {
        Text(
            modifier = Modifier.clickable { onTermsOfServiceClicked() },
            text = "View our Terms and Conditions",
            style =
                MaterialTheme.typography.caption.copy(
                    color = MaterialTheme.colors.primary,
                ),
        )
        Text(
            modifier =
                Modifier.clickable { onPrivacyPolicyClicked() }
                    .padding(
                        top = if (isPortrait) 8.dp else 0.dp,
                        start = if (isPortrait) 0.dp else 8.dp,
                    ),
            text = "View our Privacy Policy",
            style =
                MaterialTheme.typography.caption.copy(
                    color = MaterialTheme.colors.primary,
                ),
        )
      }

  if (isPortrait) {
    Column(
        modifier = modifier.padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) { content() }
  } else {
    Row(
        modifier = modifier.padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) { content() }
  }
}

@Composable
private fun Disclosure(
    modifier: Modifier = Modifier,
    name: String,
) {
  val scrollState = rememberScrollState()
  Column(
      modifier = modifier.verticalScroll(scrollState).padding(16.dp),
  ) {
    Text(
        text = "$name is free and open source software.",
        style = MaterialTheme.typography.body1,
    )

    Text(
        modifier = Modifier.padding(top = 8.dp),
        text =
            """
        Because it is distributed on the Google Play Store, the developer is provided
        by default with certain analytics related to your usage of the application called Vitals.
        You can opt out of these analytics from your device's system settings."""
                .trimIndent()
                .replace("\n", " "),
        style = MaterialTheme.typography.body2,
    )

    Text(
        modifier = Modifier.padding(top = 8.dp),
        text =
            """
              Aside from these Google Play Store Vitals, your application data is never knowingly
              collected, shared, transported, or shown to any other party, including the developer.
              """
                .trimIndent()
                .replace("\n", " "),
        style = MaterialTheme.typography.body2,
    )
  }
}

@Composable
private fun Actions(
    modifier: Modifier = Modifier,
    isPortrait: Boolean,
    onAccept: () -> Unit,
    onReject: () -> Unit,
) {
  val content =
      @Composable
      {
        Button(
            onClick = onAccept,
        ) {
          Text(
              text = stringResource(R.string.dpd_accept),
          )
        }
        TextButton(
            modifier =
                Modifier.padding(
                    top = if (isPortrait) 8.dp else 0.dp,
                    start = if (isPortrait) 0.dp else 8.dp,
                ),
            onClick = onReject,
        ) {
          Text(
              text = stringResource(R.string.dpd_reject),
              fontSize = 12.sp,
          )
        }
      }

  if (isPortrait) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) { content() }
  } else {
    Row(
        modifier = modifier.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) { content() }
  }
}

@Composable
private fun NavigationError(
    snackbarHostState: SnackbarHostState,
    error: Throwable?,
    onSnackbarDismissed: () -> Unit,
) {
  SnackbarHost(hostState = snackbarHostState)

  if (error != null) {
    LaunchedEffect(error) {
      snackbarHostState.showSnackbar(
          message = error.message ?: "An unexpected error occurred",
          duration = SnackbarDuration.Long,
      )
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
            icon = 0
            name = "TEST"
            navigationError = null
          },
      imageLoader = createNewTestImageLoader(),
      onPrivacyPolicyClicked = {},
      onTermsOfServiceClicked = {},
      onNavigationErrorDismissed = {},
      onAccept = {},
      onReject = {},
  )
}
