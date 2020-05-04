package com.pyamsoft.pydroid.ui.otherapps

import androidx.fragment.app.Fragment
import com.pyamsoft.pydroid.ui.settings.AppSettingsPopoutDialog

internal class OtherAppsDialog : AppSettingsPopoutDialog() {

    override fun getContents(): Fragment {
        return OtherAppsFragment.newInstance()
    }

    override fun getInitialTitle(): String {
        return "More pyamsoft apps"
    }

    companion object {
        internal const val TAG = "OtherAppsDialog"
    }
}