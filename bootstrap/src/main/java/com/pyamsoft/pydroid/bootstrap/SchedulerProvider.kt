package com.pyamsoft.pydroid.bootstrap

import androidx.annotation.CheckResult
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

interface SchedulerProvider {

  @get:CheckResult
  val backgroundScheduler: Scheduler

  @get:CheckResult
  val foregroundScheduler: Scheduler

  object DEFAULT : SchedulerProvider {

    override val backgroundScheduler: Scheduler = Schedulers.io()

    override val foregroundScheduler: Scheduler = AndroidSchedulers.mainThread()

  }
}
