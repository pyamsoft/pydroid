package com.pyamsoft.pydroid.ui.about

import android.view.View
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.pyamsoft.pydroid.bootstrap.libraries.OssLibrary
import com.pyamsoft.pydroid.ui.databinding.AdapterItemAboutBinding
import com.pyamsoft.pydroid.ui.util.setOnDebouncedClickListener
import com.pyamsoft.pydroid.ui.util.show

internal class RealViewHolder internal constructor(
  private val binding: AdapterItemAboutBinding,
  private val activity: FragmentActivity
) : ViewHolder(binding.root) {

  override fun bind(model: OssLibrary) {
    binding.apply {
      aboutLibraryTitle.text = model.name
      aboutLibraryLicense.text = "License: ${model.licenseName}"
      aboutLibraryDescription.text = model.description
      aboutLibraryDescription.isVisible = model.description.isNotBlank()

      aboutLibraryVisitHomepage.setOnDebouncedClickListener {
        ViewLicenseDialog.newInstance(model.name, model.libraryUrl)
            .show(activity, ViewLicenseDialog.TAG)
      }

      aboutLibraryViewLicense.setOnDebouncedClickListener {
        ViewLicenseDialog.newInstance(model.name, model.licenseUrl)
            .show(activity, ViewLicenseDialog.TAG)
      }
    }
  }

  override fun unbind() {
    binding.apply {
      aboutLibraryTitle.text = null
      aboutLibraryLicense.text = null
      aboutLibraryDescription.text = null
      aboutLibraryDescription.isGone = true
      aboutLibraryVisitHomepage.setOnDebouncedClickListener(null)
      aboutLibraryViewLicense.setOnDebouncedClickListener(null)

      unbind()
    }
  }

}

internal class FakeViewHolder(itemView: View) : ViewHolder(itemView)

internal abstract class ViewHolder internal constructor(
  itemView: View
) : RecyclerView.ViewHolder(itemView) {

  open fun bind(model: OssLibrary) {

  }

  open fun unbind() {

  }

}
