/*
 * Copyright 2019 Peter Kenji Yamanaka
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
 *
 */

package com.pyamsoft.pydroid.ui.about.dialog

import android.graphics.Bitmap
import android.webkit.WebResourceRequest
import android.webkit.WebView
import androidx.webkit.WebResourceErrorCompat
import androidx.webkit.WebViewClientCompat
import androidx.webkit.WebViewFeature
import com.pyamsoft.pydroid.core.bus.EventBus
import com.pyamsoft.pydroid.ui.about.dialog.UrlWebviewState.ExternalNavigation
import com.pyamsoft.pydroid.ui.about.dialog.UrlWebviewState.Loading
import com.pyamsoft.pydroid.ui.about.dialog.UrlWebviewState.PageError
import com.pyamsoft.pydroid.ui.about.dialog.UrlWebviewState.PageLoaded
import timber.log.Timber

internal class UrlWebViewClient internal constructor(
  private val debug: Boolean,
  private val link: String,
  private val bus: EventBus<UrlWebviewState>,
  private val isVisible: () -> Boolean
) : WebViewClientCompat() {

  override fun onPageStarted(
    view: WebView?,
    url: String?,
    favicon: Bitmap?
  ) {
    super.onPageStarted(view, url, favicon)
    Timber.d("Start loading url: $url")
    val fixedUrl = url?.trimEnd('/')
        .orEmpty()
    val isTarget = (fixedUrl == link) || (url == link)
    if (isTarget) {
      Timber.d("Started loading target.")
      bus.publish(Loading)
    }
  }

  override fun onPageFinished(
    view: WebView,
    url: String
  ) {
    super.onPageFinished(view, url)
    Timber.d("Loaded url: $url, looking for $link")
    val fixedUrl = url.trimEnd('/')
    val isTarget = (fixedUrl == link) || (url == link)
    if (isTarget) {
      Timber.d("Loaded target url: $fixedUrl, show layoutRoot")
      bus.publish(PageLoaded(fixedUrl))
    }

    // If we are showing the layoutRoot and we've navigated off the url, close the dialog
    if (isVisible() && fixedUrl != link) {
      Timber.w("Navigated away from page: $fixedUrl - close dialog, and open browser")
      bus.publish(ExternalNavigation(fixedUrl))
    }
  }

  override fun onReceivedError(
    view: WebView,
    request: WebResourceRequest,
    error: WebResourceErrorCompat
  ) {
    super.onReceivedError(view, request, error)
    if (debug) {
      logWebviewError(error)
    }
    val pageUrl = request.url.toString()

    val fixedUrl = pageUrl.trimEnd('/')
    val isTarget = (fixedUrl == link) || (pageUrl == link)
    if (isTarget) {
      Timber.w("Webview error occurred but target page still reached.")
    }
    bus.publish(PageError(fixedUrl))
  }

  private fun logWebviewError(error: WebResourceErrorCompat) {
    val errorCode: Int
    if (WebViewFeature.isFeatureSupported(WebViewFeature.WEB_RESOURCE_ERROR_GET_CODE)) {
      errorCode = error.errorCode
    } else {
      errorCode = -1
    }
    val errorDescription: CharSequence
    if (WebViewFeature.isFeatureSupported(WebViewFeature.WEB_RESOURCE_ERROR_GET_DESCRIPTION)) {
      errorDescription = error.description
    } else {
      errorDescription = "(null)"
    }
    Timber.e("Webview error [$errorCode]: $errorDescription")
  }

}
