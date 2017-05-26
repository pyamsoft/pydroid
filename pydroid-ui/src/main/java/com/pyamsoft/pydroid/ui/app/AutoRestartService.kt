/*
 * Copyright 2017 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.ui.app

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.SystemClock
import android.support.annotation.CheckResult
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

  override fun onTaskRemoved(rootIntent: Intent) {
    super.onTaskRemoved(rootIntent)
    if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT && isAutoRestartEnabled) {
      Timber.w(
          "Android KitKat is affected by a bug which causing START_STICKY " + "and Foreground services to be killed onTaskRemoved.")
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

  /**
   * Change to disable auto restart ability
   */
  protected val isAutoRestartEnabled: Boolean
    @CheckResult get() = true
}
