/*
 * Copyright 2016 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.app.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

public abstract class AutoRestartService extends Service {

  private AlarmManager alarmManager;

  @Override public void onCreate() {
    super.onCreate();
    alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
  }

  // Allow for restarting the service on KitKat when the task is removed. There is a bug that
  // otherwise prevents this from working. Ugly hack workaround should fix most cases.
  @Override public void onTaskRemoved(final @NonNull Intent rootIntent) {
    super.onTaskRemoved(rootIntent);
    final PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 1,
        new Intent(getApplicationContext(), getServiceClass()), PendingIntent.FLAG_UPDATE_CURRENT);
    alarmManager.cancel(pendingIntent);
    alarmManager.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 4000,
        pendingIntent);
  }

  @NonNull @CheckResult protected abstract Class<? extends AutoRestartService> getServiceClass();
}
