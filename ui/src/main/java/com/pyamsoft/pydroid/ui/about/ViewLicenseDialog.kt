package com.pyamsoft.pydroid.ui.about

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.CheckResult
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.pyamsoft.pydroid.loader.ImageLoader
import com.pyamsoft.pydroid.loader.ImageTarget
import com.pyamsoft.pydroid.ui.PYDroid
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.app.fragment.ToolbarDialog
import com.pyamsoft.pydroid.ui.app.fragment.requireArguments
import com.pyamsoft.pydroid.ui.databinding.DialogWebviewBinding
import com.pyamsoft.pydroid.ui.util.DebouncedOnClickListener
import com.pyamsoft.pydroid.ui.util.navigate
import com.pyamsoft.pydroid.ui.util.setUpEnabled
import com.pyamsoft.pydroid.util.hyperlink
import com.pyamsoft.pydroid.util.tintWith

internal class ViewLicenseDialog : ToolbarDialog() {

  private lateinit var binding: DialogWebviewBinding
  private lateinit var link: String
  private var isLightToolbar = true

  internal lateinit var imageLoader: ImageLoader

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    isCancelable = false
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    requireArguments().also {
      link = it.getString(LINK, "")
    }

    PYDroid.obtain(requireContext())
        .inject(this)

    binding = DialogWebviewBinding.inflate(inflater, container, false)
    val view = binding.root

    inflateToolbarMenu(view)
    setupWebview()

    return view
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
      webview.webViewClient = object : WebViewClient() {

        override fun onLoadResource(
          view: WebView?,
          url: String?
        ) {
          super.onLoadResource(view, url)
          if (url == link) {
            showWebView()
          }
        }

        override fun onReceivedError(
          view: WebView?,
          request: WebResourceRequest?,
          error: WebResourceError?
        ) {
          super.onReceivedError(view, request, error)
          showWebView()
        }

      }
      webview.loadUrl(link)
    }
  }

  private fun inflateToolbarMenu(view: View) {
    binding.apply {
      toolbar.title = "Open Source License"

      // Inflate menu
      toolbar.inflateMenu(R.menu.oss_library_menu)

      // Set up back navigation and custom icon
      toolbar.setNavigationOnClickListener(DebouncedOnClickListener.create {
        dismiss()
      })

      imageLoader.load(R.drawable.ic_close_24dp)
          .mutate {
            if (!isLightToolbar) {
              return@mutate it.tintWith(requireActivity(), R.color.black)
            } else {
              return@mutate it
            }
          }
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
          .bind(viewLifecycleOwner)

      // Bind menu item to open externally
      toolbar.menu.findItem(R.id.menu_item_view_license)
          .setOnMenuItemClickListener {
            link.hyperlink(view.context)
                .navigate(view)
            return@setOnMenuItemClickListener true
          }

      // Tint icon if needed
      val openExternally = toolbar.menu.findItem(R.id.menu_item_view_license)
      val icon = openExternally.icon
      if (isLightToolbar) {
        icon.mutate()
            .also {
              val tintedIcon = it.tintWith(requireActivity(), R.color.white)
              openExternally.icon = tintedIcon
            }
      } else {
        toolbar.setTitleTextColor(ContextCompat.getColor(view.context, R.color.black))
      }
    }

  }

  override fun onResume() {
    super.onResume()
    // The dialog is super small for some reason. We have to set the size manually, in onResume
    dialog.window?.setLayout(
        WindowManager.LayoutParams.MATCH_PARENT,
        WindowManager.LayoutParams.MATCH_PARENT
    )
  }

  companion object {

    internal const val TAG = "ViewLicenseDialog"
    private const val LINK = "link"
    private const val LIGHT_TOOLBAR = "light_toolbar"

    @CheckResult
    @JvmStatic
    fun newInstance(
      link: String,
      isLightToolbar: Boolean
    ): ViewLicenseDialog {
      return ViewLicenseDialog().apply {
        arguments = Bundle().apply {
          putString(LINK, link)
          putBoolean(LIGHT_TOOLBAR, isLightToolbar)
        }
      }
    }
  }
}
