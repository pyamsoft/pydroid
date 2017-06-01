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

package com.pyamsoft.pydroid.bus

import android.support.annotation.CheckResult
import com.pyamsoft.pydroid.helper.Optional
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import timber.log.Timber

class EventBus private constructor() {

  private val bus: Subject<Optional<*>> = PublishSubject.create()

  /**
   * Publish an event to a registered Receiver class
   *
   * The bus does not make any restrictions on what type an Event should be. While events can be
   * mutable, it is recommended to make your Event object immutable as the bus makes no guarantees
   * about the state of the data
   *
   * Only class types registered by the publish function will be eligible to receive bus events
   *
   * At least one class type must be passed in with the publish call. Duplicate class types will be
   * ignored
   */
  fun <T> publish(event: T) {
    if (bus.hasObservers()) {
      bus.onNext(Optional.of(event))
    } else {
      Timber.w("No observers on bus, ignore publish event: %s", event)
    }
  }


  /**
   * Listen for Bus events
   *
   * Only a class which has been registered as a listener via a publish function call can receive
   * publish events from the EventBus.
   *
   * Any class which was not registered in a publish event will receive an empty stream
   */
  @CheckResult fun <T> listen(eventClass: Class<out T>): Observable<out T> {
    return bus.onErrorReturn({
      Timber.e(it, "Error on EventBus. EventBus ignores error, continues with blank")
      Optional.ofNullable(null)
    }).filter { it.isPresent() }.map { it.item() }.filter {
      eventClass.isAssignableFrom(it!!::class.java)
    }.map { eventClass.cast(it) }
  }

  companion object {

    @JvmStatic @Volatile private var instance: EventBus? = null

    /**
     * Create a new local bus instance to use
     */
    @JvmStatic @CheckResult fun newLocalBus(): EventBus {
      return EventBus()
    }

    /**
     * Lazy load and return the EventBus
     */
    @JvmStatic @CheckResult fun get(): EventBus {
      if (instance == null) {
        synchronized(EventBus::class) {
          if (instance == null) {
            instance = newLocalBus()
          }
        }
      }

      return instance!!
    }
  }
}

