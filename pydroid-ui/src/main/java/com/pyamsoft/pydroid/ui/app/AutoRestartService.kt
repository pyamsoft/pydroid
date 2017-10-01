/*
 *     Copyright (C) 2017 Peter Kenji Yamanaka
 *
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.pyamsoft.pydroid.ui.app

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.SystemClock
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * Android API 19 has a bug where a service, even a foreground one, will stop when swiped away or
 * task
 * killed, even if START_STICKY is set
 *
 * This service is configured to auto restart itself when it is destroyed via onTaskRemoved, and
 * will
 * only reliably work with Foreground services. Background services may also work, but since these
 * are
 * generally limited in Android O, you should work to move away from them as best as possible.
 */
abstract class AutoRestartService : Service() {

  /**
   * Change to disable auto restart ability
   */
  protected open val isAutoRestartEnabled: Boolean = true

  override fun onTaskRemoved(rootIntent: Intent) {
    super.onTaskRemoved(rootIntent)
    if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT && isAutoRestartEnabled) {
      Timber.w("""
        |Android KitKat is affected by a bug which causes START_STICKY
        |and Foreground services to be killed onTaskRemoved.
      """.trimMargin())
      Timber.w(
          "The service will schedule an Alarm for 5 seconds out to automatically restart itself")

      val restartIntent = Intent(applicationContext, javaClass)
      restartIntent.`package` = packageName

      // We must use a non-zero value for the RC, Samsung seems to like things over 1000
      val pendingIntent = PendingIntent.getService(applicationContext, 1024, restartIntent,
          PendingIntent.FLAG_ONE_SHOT)

      val alarmManager = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager

      // Cancel any old alarms
      alarmManager.cancel(pendingIntent)
      pendingIntent.cancel()

      // Schedule alarm
      val restartTime = SystemClock.elapsedRealtime() + TimeUnit.SECONDS.toMillis(5)
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        alarmManager.setAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME, restartTime, pendingIntent)
      } else {
        alarmManager.set(AlarmManager.ELAPSED_REALTIME, restartTime, pendingIntent)
      }
    }
  }
}
