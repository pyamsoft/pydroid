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

package com.pyamsoft.pydroid.bus

import android.support.annotation.CheckResult
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import timber.log.Timber

class RxBus<T : Any> private constructor() : EventBus<T> {

  private val bus: Subject<T> = PublishSubject.create()

  override fun publish(event: T) {
    if (bus.hasObservers()) {
      bus.onNext(event)
    } else {
      Timber.w("No observers on bus, ignore publish event: %s", event)
    }
  }

  override fun listen(): Observable<T> = bus

  companion object {

    /**
     * Create a new local bus instance to use
     */
    @JvmStatic
    @CheckResult
    fun <T : Any> create(): EventBus<T> = RxBus()

  }
}

