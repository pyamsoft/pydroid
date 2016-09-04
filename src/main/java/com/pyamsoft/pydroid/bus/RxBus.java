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

package com.pyamsoft.pydroid.bus;

import android.support.annotation.NonNull;
import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

public abstract class RxBus<T> {

  @NonNull private final Subject<T, T> bus = new SerializedSubject<>(PublishSubject.create());

  public void post(@NonNull T event) {
    if (bus.hasObservers()) {
      bus.onNext(event);
    }
  }

  @NonNull public Observable<T> register() {
    return bus.filter(confirmationEvent -> confirmationEvent != null);
  }
}
