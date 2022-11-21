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

package com.pyamsoft.pydroid.ui.internal.billing

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import coil.ImageLoader
import com.pyamsoft.pydroid.billing.BillingSku
import com.pyamsoft.pydroid.billing.BillingState
import com.pyamsoft.pydroid.theme.keylines
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.defaults.DialogDefaults
import com.pyamsoft.pydroid.ui.internal.app.AppHeader
import com.pyamsoft.pydroid.ui.internal.test.createNewTestImageLoader

@Composable
internal fun BillingScreen(
    modifier: Modifier = Modifier,
    state: BillingViewState,
    imageLoader: ImageLoader,
    onPurchase: (BillingSku) -> Unit,
    onBillingErrorDismissed: () -> Unit,
    onClose: () -> Unit,
) {
  val snackbarHostState = remember { SnackbarHostState() }

  // Scroll on small
  AppHeader(
      modifier = modifier,
      elevation = DialogDefaults.Elevation,
      icon = state.icon,
      imageLoader = imageLoader,
  ) {
    item {
      Text(
          modifier = Modifier.fillMaxWidth(),
          text = state.name,
          style =
              MaterialTheme.typography.h5.copy(
                  textAlign = TextAlign.Center,
              ),
      )
    }

    item {
      Crossfade(
          targetState = state.connected,
      ) { connected ->
        // Remember computed value
        val isLoading = remember { connected == BillingState.LOADING }
        val isConnected = remember { connected == BillingState.CONNECTED }

        if (isLoading) {
          Loading(
              modifier = Modifier.fillMaxWidth(),
          )
        } else {
          SkuList(
              isConnected = isConnected,
              list = state.skuList,
              onPurchase = onPurchase,
          )
        }
      }
    }

    item {
      Row(
          modifier = Modifier.padding(MaterialTheme.keylines.content),
      ) {
        Spacer(
            modifier = Modifier.weight(1F),
        )
        TextButton(
            onClick = onClose,
        ) {
          Text(
              text = stringResource(R.string.close),
          )
        }
      }
    }

    item {
      BillingError(
          snackbarHostState = snackbarHostState,
          error = state.error,
          onSnackbarDismissed = onBillingErrorDismissed,
      )
    }
  }
}

@Composable
private fun SkuList(
    modifier: Modifier = Modifier,
    isConnected: Boolean,
    list: List<BillingSku>,
    onPurchase: (BillingSku) -> Unit,
) {
  // Remember the computed ready state
  val readyState = remember {
    ReadyState(
        isConnected = isConnected,
        isEmpty = list.isEmpty(),
    )
  }

  Crossfade(
      modifier = modifier,
      targetState = readyState,
  ) { ready ->
    if (ready.isEmpty || !isConnected) {
      ErrorText(
          modifier = Modifier.fillMaxWidth(),
      )
    } else {
      Column(
          modifier = Modifier.fillMaxWidth(),
      ) {
        list.forEach { item ->
          BillingListItem(
              modifier = Modifier.fillMaxWidth(),
              sku = item,
              onPurchase = onPurchase,
          )
        }
      }
    }
  }
}

@Composable
private fun ErrorText(
    modifier: Modifier = Modifier,
) {
  Box(
      modifier = modifier.padding(MaterialTheme.keylines.content),
      contentAlignment = Alignment.Center,
  ) {
    Text(
        text = stringResource(R.string.billing_error_message),
        style = MaterialTheme.typography.body1)
  }
}

@Composable
private fun Loading(
    modifier: Modifier = Modifier,
) {
  Box(
      modifier = modifier.padding(MaterialTheme.keylines.content),
      contentAlignment = Alignment.Center,
  ) {
    CircularProgressIndicator()
  }
}

@Composable
private fun BillingError(
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
      val message = error.message
      snackbarHostState.showSnackbar(
          message = if (message.isNullOrBlank()) "An unexpected error occurred" else message,
          duration = SnackbarDuration.Short,
      )
      onSnackbarDismissed()
    }
  }
}

private data class ReadyState(
    val isConnected: Boolean,
    val isEmpty: Boolean,
)

private val PREVIEW_SKUS =
    listOf(
        object : BillingSku {
          override val id: String = "test"
          override val displayPrice: String = "$10.00"
          override val price: Long = 1000
          override val title: String = "TEST"
          override val description: String = "JUST A TEST"
        },
        object : BillingSku {
          override val id: String = "test2"
          override val displayPrice: String = "$20.00"
          override val price: Long = 2000
          override val title: String = "TEST AGAIN"
          override val description: String = "JUST ANOTHER TEST"
        },
    )

@Composable
private fun PreviewBillingScreen(
    connected: BillingState,
    skuList: List<BillingSku>,
    error: Throwable?,
) {
  BillingScreen(
      state =
          MutableBillingViewState().apply {
            this.name = "TEST APPLICATION"
            this.connected = connected
            this.skuList = skuList
            this.error = error
          },
      imageLoader = createNewTestImageLoader(),
      onPurchase = {},
      onBillingErrorDismissed = {},
      onClose = {},
  )
}

@Preview
@Composable
private fun PreviewBillingScreenConnectedWithNoListNoError() {
  PreviewBillingScreen(
      connected = BillingState.CONNECTED,
      skuList = emptyList(),
      error = null,
  )
}

@Preview
@Composable
private fun PreviewBillingScreenConnectedWithNoListError() {
  PreviewBillingScreen(
      connected = BillingState.CONNECTED,
      skuList = emptyList(),
      error = RuntimeException("TEST"),
  )
}

@Preview
@Composable
private fun PreviewBillingScreenConnectedWithListError() {
  PreviewBillingScreen(
      connected = BillingState.CONNECTED,
      skuList = PREVIEW_SKUS,
      error = RuntimeException("TEST"),
  )
}

@Preview
@Composable
private fun PreviewBillingScreenConnectedWithListNoError() {
  PreviewBillingScreen(
      connected = BillingState.CONNECTED,
      skuList = PREVIEW_SKUS,
      error = null,
  )
}

@Preview
@Composable
private fun PreviewBillingScreenDisconnectedWithListError() {
  PreviewBillingScreen(
      connected = BillingState.DISCONNECTED,
      skuList = PREVIEW_SKUS,
      error = RuntimeException("TEST"),
  )
}

@Preview
@Composable
private fun PreviewBillingScreenDisconnectedWithListNoError() {
  PreviewBillingScreen(
      connected = BillingState.DISCONNECTED,
      skuList = PREVIEW_SKUS,
      error = null,
  )
}

@Preview
@Composable
private fun PreviewBillingScreenDisconnectedWithNoListNoError() {
  PreviewBillingScreen(
      connected = BillingState.DISCONNECTED,
      skuList = emptyList(),
      error = null,
  )
}

@Preview
@Composable
private fun PreviewBillingScreenDisconnectedWithNoListError() {
  PreviewBillingScreen(
      connected = BillingState.DISCONNECTED,
      skuList = emptyList(),
      error = RuntimeException("TEST"),
  )
}

@Preview
@Composable
private fun PreviewBillingScreenLoadingWithListError() {
  PreviewBillingScreen(
      connected = BillingState.LOADING,
      skuList = PREVIEW_SKUS,
      error = RuntimeException("TEST"),
  )
}

@Preview
@Composable
private fun PreviewBillingScreenLoadingWithListNoError() {
  PreviewBillingScreen(
      connected = BillingState.LOADING,
      skuList = PREVIEW_SKUS,
      error = null,
  )
}

@Preview
@Composable
private fun PreviewBillingScreenLoadingWithNoListNoError() {
  PreviewBillingScreen(
      connected = BillingState.LOADING,
      skuList = emptyList(),
      error = null,
  )
}

@Preview
@Composable
private fun PreviewBillingScreenLoadingWithNoListError() {
  PreviewBillingScreen(
      connected = BillingState.LOADING,
      skuList = emptyList(),
      error = RuntimeException("TEST"),
  )
}
