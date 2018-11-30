package com.pyamsoft.pydroid.ui.about

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Build.VERSION_CODES
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle.Event.ON_DESTROY
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.pyamsoft.pydroid.loader.ImageLoader
import com.pyamsoft.pydroid.loader.ImageTarget
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.databinding.DialogWebviewBinding
import com.pyamsoft.pydroid.ui.util.DebouncedOnClickListener
import com.pyamsoft.pydroid.ui.util.Snackbreak
import com.pyamsoft.pydroid.ui.util.setUpEnabled
import com.pyamsoft.pydroid.util.hyperlink
import timber.log.Timber

internal class LicenseViewImpl internal constructor(
  private val inflater: LayoutInflater,
  private val container: ViewGroup?,
  private val owner: LifecycleOwner,
  private val imageLoader: ImageLoader,
  private val link: String,
  private val name: String
) : LicenseView, LifecycleObserver {

  private lateinit var binding: DialogWebviewBinding

  init {
    owner.lifecycle.addObserver(this)
  }

  override fun create() {
    binding = DialogWebviewBinding.inflate(inflater, container, false)
    inflateToolbarMenu()
    setupWebview()
  }

  override fun loadView(onDismiss: () -> Unit) {
    // Set up back navigation and custom icon
    binding.apply {
      toolbar.setNavigationOnClickListener(DebouncedOnClickListener.create {
        onDismiss()
      })

      webview.webViewClient = object : WebViewClient() {

        override fun onPageFinished(
          view: WebView,
          url: String
        ) {
          super.onPageFinished(view, url)
          val fixedUrl = url.trimEnd('/')
          if (fixedUrl == link) {
            Timber.d("Loaded url: $url, show webview")
            showWebView()
          }

          // If we are showing the webview and we've navigated off the url, close the dialog
          if (webview.isVisible && fixedUrl != link) {
            Timber.w("Navigated away from page: $url - close dialog, and open extenally")
            val error = fixedUrl.hyperlink(view.context)
                .navigate()

            if (error == null) {
              onDismiss()
            } else {
              Snackbreak.short(root(), "No application can handle this URL")
                  .show()
            }
          }
        }

        @RequiresApi(VERSION_CODES.M)
        override fun onReceivedError(
          view: WebView,
          request: WebResourceRequest,
          error: WebResourceError
        ) {
          super.onReceivedError(view, request, error)
          if (request.url.toString() == link) {
            Timber.e("Webview error: ${error.errorCode} ${error.description}")
            showWebView()
          }
        }

        @Suppress("DEPRECATION", "OverridingDeprecatedMember")
        override fun onReceivedError(
          view: WebView,
          errorCode: Int,
          description: String?,
          failingUrl: String?
        ) {
          super.onReceivedError(view, errorCode, description, failingUrl)
          if (failingUrl == link) {
            Timber.e("Webview error: $errorCode $description")
            showWebView()
          }
        }

      }
      webview.loadUrl(link)
    }
  }

  @Suppress("unused")
  @OnLifecycleEvent(ON_DESTROY)
  internal fun destroy() {
    owner.lifecycle.removeObserver(this)

    binding.apply {
      unbind()
    }
  }

  override fun root(): View {
    return binding.root
  }

  private fun showWebView() {
    binding.apply {
      spinner.isGone = true
      webview.isVisible = true
    }
  }

  @SuppressLint("SetJavaScriptEnabled")
  private fun setupWebview() {
    binding.apply {
      webview.settings.javaScriptEnabled = true
    }
  }

  private fun inflateToolbarMenu() {
    binding.apply {
      toolbar.title = name

      // Inflate menu
      toolbar.inflateMenu(R.menu.oss_library_menu)

      imageLoader.load(R.drawable.ic_close_24dp)
          .into(object : ImageTarget<Drawable> {

            override fun view(): View {
              return toolbar
            }

            override fun clear() {
              toolbar.navigationIcon = null
            }

            override fun setImage(image: Drawable) {
              toolbar.setUpEnabled(true, image)
            }

            override fun setError(error: Drawable?) {
              toolbar.setUpEnabled(false)
            }

            override fun setPlaceholder(placeholder: Drawable?) {
              toolbar.setUpEnabled(false)
            }

          })
          .bind(owner)
    }

  }

  override fun onMenuItemClick(onClick: () -> Unit) {
    // Bind menu item to open externally
    binding.toolbar.menu.findItem(R.id.menu_item_view_license)
        .setOnMenuItemClickListener {
          onClick()
          return@setOnMenuItemClickListener true
        }
  }

}