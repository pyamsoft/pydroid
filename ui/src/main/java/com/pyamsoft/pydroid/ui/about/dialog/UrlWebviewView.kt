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
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle.Event.ON_PAUSE
import androidx.lifecycle.Lifecycle.Event.ON_RESUME
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.pyamsoft.pydroid.arch.BaseUiView
import com.pyamsoft.pydroid.arch.UiToggleView
import com.pyamsoft.pydroid.core.bus.EventBus
import com.pyamsoft.pydroid.ui.R

internal class UrlWebviewView internal constructor(
  private val owner: LifecycleOwner,
  private val debug: Boolean,
  private val link: String,
  private val bus: EventBus<UrlWebviewState>,
  parent: ViewGroup
) : BaseUiView<Unit>(parent, Unit),
    UiToggleView, LifecycleObserver {

  override val layout: Int = R.layout.license_webview

  override val layoutRoot by lazyView<WebView>(R.id.license_webview)

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
    layoutRoot.onResume()
  }

  @Suppress("unused")
  @OnLifecycleEvent(ON_PAUSE)
  internal fun onPause() {
    layoutRoot.onPause()
  }

  private fun setupWebview() {
    layoutRoot.webViewClient = UrlWebViewClient(debug, link, bus) { layoutRoot.isVisible }
    layoutRoot.loadUrl(link)
  }

  @SuppressLint("SetJavaScriptEnabled")
  private fun setupWebviewJavascript() {
    layoutRoot.settings.javaScriptEnabled = true
  }

  override fun show() {
    layoutRoot.isVisible = true
  }

  override fun hide() {
    layoutRoot.isVisible = false
  }

  override fun onTeardown() {
    owner.lifecycle.removeObserver(this)
    layoutRoot.destroy()
  }

}
