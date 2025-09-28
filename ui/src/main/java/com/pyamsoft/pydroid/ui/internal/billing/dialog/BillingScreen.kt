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

package com.pyamsoft.pydroid.ui.internal.billing.dialog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.ImageLoader
import com.pyamsoft.pydroid.billing.BillingPurchase
import com.pyamsoft.pydroid.billing.BillingSku
import com.pyamsoft.pydroid.billing.BillingState
import com.pyamsoft.pydroid.theme.keylines
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.haptics.LocalHapticManager
import com.pyamsoft.pydroid.ui.internal.app.AppHeader
import com.pyamsoft.pydroid.ui.internal.test.createNewTestImageLoader
import com.pyamsoft.pydroid.ui.util.collectAsStateListWithLifecycle

private enum class BillingScreenItems {
  LOADING,
  ERROR,
  ITEMS,
}

@Composable
internal fun BillingScreen(
    modifier: Modifier = Modifier,
    state: BillingDialogViewState,
    imageLoader: ImageLoader,
    onPurchase: (BillingSku) -> Unit,
    onBillingPopupDismissed: () -> Unit,
    onClose: () -> Unit,
) {
  val skuList = state.skuList.collectAsStateListWithLifecycle()
  val connected by state.connected.collectAsStateWithLifecycle()

  val icon by state.icon.collectAsStateWithLifecycle()
  val name by state.name.collectAsStateWithLifecycle()

  val error by state.error.collectAsStateWithLifecycle()
  val thanksPurchase by state.thanksPurchase.collectAsStateWithLifecycle()

  // Remember computed value
  val isLoading = remember(connected) { connected == BillingState.LOADING }
  val isConnected = remember(connected) { connected == BillingState.CONNECTED }
  val isError =
      remember(
          isConnected,
          skuList,
      ) {
        skuList.isEmpty() || !isConnected
      }

  AppHeader(
      modifier = modifier,
      icon = icon,
      name = name,
      imageLoader = imageLoader,
      afterScroll = {
        val snackbarHostState = remember { SnackbarHostState() }

        BillingPopup(
            modifier = Modifier.fillMaxWidth(),
            snackbarHostState = snackbarHostState,
            error = error,
            purchase = thanksPurchase,
            onDismiss = onBillingPopupDismissed,
        )

        ActionRow(
            modifier = Modifier.padding(MaterialTheme.keylines.baseline),
            onClose = onClose,
        )
      },
  ) {
    if (isLoading) {
      item(
          contentType = BillingScreenItems.LOADING,
      ) {
        Loading(
            modifier = Modifier.fillMaxWidth(),
        )
      }
    } else if (isError) {
      item(
          contentType = BillingScreenItems.ERROR,
      ) {
        ErrorText(
            modifier = Modifier.fillMaxWidth(),
        )
      }
    } else {
      skuList.forEach { item ->
        item(
            contentType = BillingScreenItems.ITEMS,
        ) {
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
private fun ActionRow(
    modifier: Modifier = Modifier,
    onClose: () -> Unit,
) {
  val hapticManager = LocalHapticManager.current

  Row(
      modifier = modifier,
      verticalAlignment = Alignment.CenterVertically,
  ) {
    Spacer(
        modifier = Modifier.weight(1F),
    )
    TextButton(
        onClick = {
          hapticManager?.cancelButtonPress()
          onClose()
        },
    ) {
      Text(
          text = stringResource(android.R.string.cancel),
      )
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
        style = MaterialTheme.typography.bodyLarge,
    )
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
private fun BillingPopup(
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState,
    error: Throwable?,
    purchase: BillingPurchase?,
    onDismiss: () -> Unit,
) {
  if (error != null) {
    LaunchedEffect(error) {
      val message = error.message
      snackbarHostState.showSnackbar(
          message = if (message.isNullOrBlank()) "An unexpected error occurred" else message,
          duration = SnackbarDuration.Short,
      )

      // We ignore the showSnackbar result because we don't care (no actions)
      onDismiss()
    }
  }

  if (purchase != null) {
    val context = LocalContext.current

    LaunchedEffect(
        context,
        purchase,
    ) {
      val message =
          when (purchase) {
            is BillingPurchase.Fake -> "Fake purchase ${purchase.sku.title}"
            is BillingPurchase.PlayBillingConsumed -> context.getString(R.string.billing_thank_you)
            /*
             * TODO(Peter): convert to a sealed interface
             *
             * We can't use a sealed interface here for some reason, as it breaks Dokka
             *
             * WARN: Could not read file: ~/PYDroid/billing/build/intermediates/compile_library_classes_jar/release/bundleLibCompileToJarRelease/classes.jar!/com/pyamsoft/pydroid/billing/BillingPurchase.class; size in bytes: 777; file type: CLASS
             * java.lang.UnsupportedOperationException: PermittedSubclasses requires ASM9
             * 	at org.jetbrains.org.objectweb.asm.ClassVisitor.visitPermittedSubclass(ClassVisitor.java:266)
             * 	at org.jetbrains.org.objectweb.asm.ClassReader.accept(ClassReader.java:684)
             * 	at org.jetbrains.org.objectweb.asm.ClassReader.accept(ClassReader.java:402)
             * 	at org.jetbrains.kotlin.load.kotlin.FileBasedKotlinClass.create(FileBasedKotlinClass.java:96)
             * 	at org.jetbrains.kotlin.load.kotlin.VirtualFileKotlinClass$Factory$create$1.invoke(VirtualFileKotlinClass.kt:67)
             * 	at org.jetbrains.kotlin.load.kotlin.VirtualFileKotlinClass$Factory$create$1.invoke(VirtualFileKotlinClass.kt:61)
             * 	at org.jetbrains.kotlin.util.PerformanceCounter.time(PerformanceCounter.kt:101)
             * 	at org.jetbrains.kotlin.load.kotlin.VirtualFileKotlinClass$Factory.create(VirtualFileKotlinClass.kt:61)
             */
            else ->
                throw AssertionError(
                    "The only reason we can't use a sealed class is because Dokka breaks."
                )
          }

      snackbarHostState.showSnackbar(
          message = message,
          duration = SnackbarDuration.Long,
      )

      // We ignore the showSnackbar result because we don't care (no actions)
      onDismiss()
    }
  }

  SnackbarHost(
      modifier = modifier,
      hostState = snackbarHostState,
  )
}

private val PREVIEW_SKUS =
    mutableStateListOf(
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
          MutableBillingDialogViewState().apply {
            this.name.value = "TEST APPLICATION"
            this.connected.value = connected
            this.skuList.value = skuList
            this.error.value = error
          },
      imageLoader = createNewTestImageLoader(),
      onPurchase = {},
      onBillingPopupDismissed = {},
      onClose = {},
  )
}

@Preview
@Composable
private fun PreviewBillingScreenConnectedWithNoListNoError() {
  PreviewBillingScreen(
      connected = BillingState.CONNECTED,
      skuList = remember { mutableStateListOf() },
      error = null,
  )
}

@Preview
@Composable
private fun PreviewBillingScreenConnectedWithNoListError() {
  PreviewBillingScreen(
      connected = BillingState.CONNECTED,
      skuList = remember { mutableStateListOf() },
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
      skuList = remember { mutableStateListOf() },
      error = null,
  )
}

@Preview
@Composable
private fun PreviewBillingScreenDisconnectedWithNoListError() {
  PreviewBillingScreen(
      connected = BillingState.DISCONNECTED,
      skuList = remember { mutableStateListOf() },
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
      skuList = remember { mutableStateListOf() },
      error = null,
  )
}

@Preview
@Composable
private fun PreviewBillingScreenLoadingWithNoListError() {
  PreviewBillingScreen(
      connected = BillingState.LOADING,
      skuList = remember { mutableStateListOf() },
      error = RuntimeException("TEST"),
  )
}
