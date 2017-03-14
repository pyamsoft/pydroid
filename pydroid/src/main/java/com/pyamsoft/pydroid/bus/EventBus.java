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
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import timber.log.Timber;

/**
 * An EventBus powered by RxJava
 */
public class EventBus {

  private static volatile EventBus instance = null;

  @NonNull private final Map<Class<? extends Event>, Set<Class<?>>> listenerMap;
  @NonNull private final Subject<Event> bus;

  private EventBus() {
    bus = PublishSubject.create();
    listenerMap = new HashMap<>();
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
  public final <T extends Event> void publish(@NonNull T event,
      @NonNull Class<?>... receiverClasses) {
    if (!bus.hasObservers()) {
      Timber.w("No observers on bus, ignore publish event: %s", event);
      return;
    }

    // Add receiver class to the set of allowed receivers for the bus for an event
    Class<? extends Event> eventClass = event.getClass();
    Set<Class<?>> classSet = listenerMap.get(eventClass);
    if (classSet == null) {
      classSet = new HashSet<>();
    }

    boolean setChanged = false;
    for (Class<?> receiverClass : receiverClasses) {
      if (classSet.add(receiverClass)) {
        if (!setChanged) {
          setChanged = true;
        }
      }
    }

    if (setChanged) {
      listenerMap.put(eventClass, classSet);
    }

    bus.onNext(event);
  }

  /**
   * Listen for Bus events
   *
   * Only a class which has been registered as a listener via a publish function call can receive
   * publish events from the EventBus.
   *
   * Any class which was not registered in a publish event will receive an empty stream
   */
  @CheckResult @NonNull public final <T extends Event> Observable<T> listen(
      @NonNull Class<T> eventClass, @NonNull Class<?> receiverClass) {
    Set<Class<?>> classSet = listenerMap.get(eventClass);
    if (classSet == null) {
      Timber.w("There are no registered receivers for: %s", eventClass.getSimpleName());
      return Observable.empty();
    }

    boolean allowed = false;
    for (Class<?> allowedClass : classSet) {
      if (allowedClass == receiverClass) {
        allowed = true;
        break;
      }
    }

    if (!allowed) {
      Timber.w("Receiver: %s is not allowed to register for %s events",
          receiverClass.getSimpleName(), eventClass.getSimpleName());
      return Observable.empty();
    }

    return bus.filter(event -> eventClass.isAssignableFrom(event.getClass())).map(eventClass::cast);
  }

  /**
   * Base implementation for all EventBus events
   */
  public interface Event {

  }
}
