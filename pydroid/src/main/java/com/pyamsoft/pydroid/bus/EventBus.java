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
 *
 */

package com.pyamsoft.pydroid.bus;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.pyamsoft.pydroid.helper.Checker;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import timber.log.Timber;

/**
 * An EventBus powered by RxJava
 *
 * Be aware of the dangers of using an Event bus. Mainly, when you publish a message there is no
 * easy way to see where it goes. Add a comment or some way to let yourself know
 */
public class EventBus {

  private static volatile EventBus instance = null;

  @NonNull private final Subject<Object> bus;

  private EventBus() {
    bus = PublishSubject.create();
  }

  /**
   * Lazy load and return the EventBus
   */
  @CheckResult @NonNull public static EventBus get() {
    if (instance == null) {
      synchronized (EventBus.class) {
        if (instance == null) {
          instance = new EventBus();
        }
      }
    }

    return instance;
  }

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
  public final <T> void publish(@NonNull T event) {
    if (!bus.hasObservers()) {
      Timber.w("No observers on bus, ignore publish event: %s", event);
      return;
    }

    bus.onNext(Checker.checkNonNull(event));
  }

  /**
   * Listen for Bus events
   *
   * Only a class which has been registered as a listener via a publish function call can receive
   * publish events from the EventBus.
   *
   * Any class which was not registered in a publish event will receive an empty stream
   */
  @CheckResult @NonNull public final <T> Observable<T> listen(@NonNull Class<T> eventClass) {
    final Class<T> listenClass = Checker.checkNonNull(eventClass);
    return bus.filter(event -> listenClass.isAssignableFrom(event.getClass()))
        .map(listenClass::cast);
  }
}
