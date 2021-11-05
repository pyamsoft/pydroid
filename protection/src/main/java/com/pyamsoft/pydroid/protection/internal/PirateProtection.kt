/*
 * Copyright 2021 Peter Kenji Yamanaka
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
 */

package com.pyamsoft.pydroid.protection.internal

import android.os.Build
import androidx.annotation.CheckResult
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.github.javiersantos.piracychecker.enums.Display
import com.github.javiersantos.piracychecker.enums.InstallerID
import com.github.javiersantos.piracychecker.piracyChecker
import com.pyamsoft.pydroid.core.Logger
import com.pyamsoft.pydroid.protection.Protection
import com.pyamsoft.pydroid.util.doOnDestroy
import com.pyamsoft.pydroid.util.isDebugMode
import com.pyamsoft.pydroid.util.valuesFromCurrentTheme

internal class PirateProtection
internal constructor(
    private val licenseKey: String,
) : Protection {
  override fun defend(activity: AppCompatActivity) {
    if (activity.application.isDebugMode()) {
      Logger.d("No pirates in debug mode yar, we be safe matey")
      return
    }

    watchForPirates(activity)
  }

  private fun watchForPirates(activity: AppCompatActivity) {
    Logger.d("Creating a piracy checker for this activity: $activity")
    val checker =
        activity
            .piracyChecker {

              // Block emulators
              enableEmulatorCheck(deepCheck = false)

              // Block debug builds (how did you get here?)
              enableDebugCheck()

              // You have to be from Google Play
              enableInstallerId(InstallerID.GOOGLE_PLAY)

              // Check the application licenseKey
              enableGooglePlayLicensing(licenseKey)

              // Color from activity
              val colors = activity.getCurrentActivityAttrs()
              withActivityColors(
                  colorPrimary = colors.colorPrimary,
                  colorPrimaryDark = colors.colorPrimaryDark,
                  withLightStatusBar = colors.isLightStatusBar,
              )

              // Show as dialog in the app
              display(Display.DIALOG)
            }
            .apply { start() }

    activity.lifecycle.doOnDestroy {
      Logger.d("Piracy checker is shutting down on Activity destroy: $activity $checker")
      checker.destroy()
    }
  }

  companion object {

    @JvmStatic
    @CheckResult
    private fun AppCompatActivity.getCurrentActivityAttrs(): ActivityAttrs {
      @ColorRes val colorPrimary: Int
      @ColorRes val colorPrimaryDark: Int
      val isLightStatusBar: Boolean

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val attrs =
            this.valuesFromCurrentTheme(
                androidx.appcompat.R.attr.colorPrimary,
                androidx.appcompat.R.attr.colorPrimaryDark,
                android.R.attr.windowLightStatusBar,
            )

        colorPrimary = ContextCompat.getColor(this, attrs[0])
        colorPrimaryDark = ContextCompat.getColor(this, attrs[1])
        isLightStatusBar = this.resources.getBoolean(attrs[2])
      } else {
        val attrs =
            this.valuesFromCurrentTheme(
                androidx.appcompat.R.attr.colorPrimary,
                androidx.appcompat.R.attr.colorPrimaryDark,
            )

        colorPrimary = ContextCompat.getColor(this, attrs[0])
        colorPrimaryDark = ContextCompat.getColor(this, attrs[1])
        isLightStatusBar = false
      }

      return ActivityAttrs(
          colorPrimary = colorPrimary,
          colorPrimaryDark = colorPrimaryDark,
          isLightStatusBar = isLightStatusBar,
      )
    }
  }

  private data class ActivityAttrs(
      @ColorRes val colorPrimary: Int,
      @ColorRes val colorPrimaryDark: Int,
      val isLightStatusBar: Boolean,
  )
}
