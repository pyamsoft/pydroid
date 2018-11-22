package com.pyamsoft.pydroid.ui.about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.ui.PYDroid
import com.pyamsoft.pydroid.ui.app.fragment.ToolbarDialog
import com.pyamsoft.pydroid.ui.app.fragment.requireArguments
import com.pyamsoft.pydroid.ui.util.navigate
import com.pyamsoft.pydroid.util.hyperlink

internal class ViewLicenseDialog : ToolbarDialog() {

  private lateinit var name: String
  private lateinit var link: String

  internal lateinit var rootView: LicenseView

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    requireArguments().also {
      name = it.getString(NAME, "")
      link = it.getString(LINK, "")
    }

    PYDroid.obtain(requireContext())
        .plusViewLicenseComponent(
            viewLifecycleOwner, inflater, container, savedInstanceState, link, name
        )
        .inject(this)
    return rootView.root()
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)
    rootView.loadView { dismiss() }

    rootView.onMenuItemClick {
      link.hyperlink(view.context)
          .navigate(view)
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
    private const val NAME = "name"
    private const val LINK = "link"

    @CheckResult
    @JvmStatic
    fun newInstance(
      name: String,
      link: String
    ): ViewLicenseDialog {
      return ViewLicenseDialog().apply {
        arguments = Bundle().apply {
          putString(NAME, name)
          putString(LINK, link)
        }
      }
    }
  }
}
