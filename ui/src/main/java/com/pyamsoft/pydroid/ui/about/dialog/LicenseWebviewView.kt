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

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle.Event.ON_PAUSE
import androidx.lifecycle.Lifecycle.Event.ON_RESUME
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.pyamsoft.pydroid.core.bus.EventBus
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.about.dialog.LicenseStateEvent.Complete
import com.pyamsoft.pydroid.ui.about.dialog.LicenseStateEvent.Loaded
import com.pyamsoft.pydroid.ui.about.dialog.LicenseStateEvent.PageError
import com.pyamsoft.pydroid.ui.arch.BaseUiView
import com.pyamsoft.pydroid.ui.arch.UiToggleView
import com.pyamsoft.pydroid.ui.arch.ViewEvent.EMPTY
import com.pyamsoft.pydroid.ui.arch.ViewEvent.EmptyBus
import com.pyamsoft.pydroid.ui.util.Toaster
import com.pyamsoft.pydroid.util.hyperlink
import timber.log.Timber

internal class LicenseWebviewView internal constructor(
  private val owner: LifecycleOwner,
  private val link: String,
  private val controllerBus: EventBus<LicenseStateEvent>,
  parent: ViewGroup
) : BaseUiView<EMPTY>(parent, EmptyBus), UiToggleView, LifecycleObserver {

  private val webview by lazyView<WebView>(R.id.license_webview)

  override val layout: Int = R.layout.license_webview

  override fun id(): Int {
    return webview.id
  }

  override fun onInflated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    owner.lifecycle.addObserver(this)

    setupWebviewJavascript()
    setupWebview()
  }

  @Suppress("unused")
  @OnLifecycleEvent(ON_RESUME)
  internal fun onResume() {
    webview.onResume()
  }

  @Suppress("unused")
  @OnLifecycleEvent(ON_PAUSE)
  internal fun onPause() {
    webview.onPause()
  }

  private fun setupWebview() {
    webview.webViewClient = object : WebViewClient() {

      override fun onPageFinished(
        view: WebView,
        url: String
      ) {
        super.onPageFinished(view, url)
        Timber.d("Loaded url: $url")
        val fixedUrl = url.trimEnd('/')
        if (fixedUrl == link) {
          Timber.d("Loaded target url: $url, show webview")
          controllerBus.publish(Loaded)
        }

        // If we are showing the webview and we've navigated off the url, close the dialog
        if (webview.isVisible && fixedUrl != link) {
          Timber.w("Navigated away from page: $url - close dialog, and open extenally")
          val error = fixedUrl.hyperlink(view.context)
              .navigate()
          controllerBus.publish(PageError(error))
        }

        controllerBus.publish(Complete)
      }

      @RequiresApi(VERSION_CODES.M)
      override fun onReceivedError(
        view: WebView,
        request: WebResourceRequest,
        error: WebResourceError
      ) {
        super.onReceivedError(view, request, error)
        Timber.e("Webview error: ${error.errorCode} ${error.description}")
        if (request.url.toString() == link) {
          controllerBus.publish(Loaded)
        }

        controllerBus.publish(Complete)
      }

      @Suppress("DEPRECATION", "OverridingDeprecatedMember")
      override fun onReceivedError(
        view: WebView,
        errorCode: Int,
        description: String?,
        failingUrl: String?
      ) {
        super.onReceivedError(view, errorCode, description, failingUrl)
        Timber.e("Webview error: $errorCode $description")
        if (failingUrl == link) {
          controllerBus.publish(Loaded)
        }

        controllerBus.publish(Complete)
      }

    }
  }

  @SuppressLint("SetJavaScriptEnabled")
  private fun setupWebviewJavascript() {
    webview.settings.javaScriptEnabled = true
  }

  override fun saveState(outState: Bundle) {
  }

  override fun show() {
    webview.isVisible = true
  }

  override fun hide() {
    webview.isVisible = false
  }

  override fun teardown() {
    webview.destroy()
    owner.lifecycle.removeObserver(this)
  }

  fun pageLoadError(error: ActivityNotFoundException?) {
    if (error != null) {
      Toaster.bindTo(owner)
          .short(webview.context, "No application can handle this URL")
          .show()
    }
  }

  fun loadUrl() {
    webview.loadUrl(link)
  }

}
