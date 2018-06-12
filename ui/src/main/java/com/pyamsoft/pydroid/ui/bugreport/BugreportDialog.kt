/*
 * Copyright (C) 2018 Peter Kenji Yamanaka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pyamsoft.pydroid.ui.bugreport

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.annotation.CheckResult
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ShareCompat
import androidx.fragment.app.FragmentActivity
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.app.activity.ActivityBase
import com.pyamsoft.pydroid.ui.app.fragment.ToolbarDialog
import com.pyamsoft.pydroid.ui.util.show
import timber.log.Timber

class BugreportDialog : ToolbarDialog() {

  private lateinit var appName: String

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    arguments!!.also {
      appName = it.getString(APP_NAME, null) ?:
          throw IllegalArgumentException("App Name cannot be NULL")
    }
  }

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    return AlertDialog.Builder(requireActivity())
        .setTitle("Submit Bug Report")
        .setMessage(
            """Ready to send a bug report to pyamsoft?
          |
          |Include as much detail as you feel will be helpful in fixing any problems you have noticed while using $appName.
          |
          |Please note that bug report related emails will not be responded to, and pyamsoft will never contact out to you regarding any submitted bug reports.
        """.trimMargin()
        )
        .setPositiveButton("Okay") { _, _ ->
          if (sendEmailReport()) {
            Timber.d("Bug Report opened - out of our hands at this point")
          } else {
            // TODO: Bugreport could not open an email interface
          }
          dismiss()
        }
        .setNegativeButton("Cancel") { _, _ -> dismiss() }
        .create()
  }

  @CheckResult
  private fun sendEmailReport(): Boolean {
    return requireActivity().let {
      val email = "pyam.soft+bugs.${it.packageName}@gmail.com"
      val intent: Intent = ShareCompat.IntentBuilder.from(it)
          .addEmailTo(email)
          .setType("text/plain")
          .setSubject("Bug Report - $appName")
          .setChooserTitle("Email Bug Report")
          .createChooserIntent()

      val canSendEmail = intent.resolveActivity(it.packageManager) != null
      if (canSendEmail) {
        it.startActivity(intent)
      }

      return@let canSendEmail
    }
  }

  companion object {

    private const val APP_NAME = "app_name"

    @JvmStatic
    @CheckResult
    fun newInstance(appName: String): BugreportDialog {
      return BugreportDialog().apply {
        arguments = Bundle().apply {
          putString(APP_NAME, appName)
        }
      }
    }

    @JvmStatic
    @CheckResult
    fun attachToToolbar(
      activity: ActivityBase,
      appName: String
    ) {
      activity.withToolbar { attachToToolbar(activity, it, appName) }
    }

    @JvmStatic
    @CheckResult
    fun attachToToolbar(
      activity: FragmentActivity,
      toolbar: Toolbar,
      appName: String
    ) {
      toolbar.apply {
        inflateMenu(R.menu.bugreport_menu)

        // Menu should be valid and contain this item, do not safe unwrap because otherwise
        // we should be crashing
        menu.findItem(R.id.menu_item_bugreport)
            .setOnMenuItemClickListener {
              BugreportDialog.newInstance(appName)
                  .show(activity, "bugreport")
              return@setOnMenuItemClickListener true
            }
      }
    }
  }
}