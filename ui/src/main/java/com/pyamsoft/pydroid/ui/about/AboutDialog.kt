package com.pyamsoft.pydroid.ui.about

import androidx.fragment.app.Fragment
import com.pyamsoft.pydroid.ui.settings.AppSettingsPopoutDialog

internal class AboutDialog : AppSettingsPopoutDialog() {

    override fun getContents(): Fragment {
        return AboutFragment.newInstance()
    }

    override fun getInitialTitle(): String {
        return "Open Source Licenses"
    }

    companion object {

        internal const val TAG = "AboutDialog"
    }

}