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

package com.pyamsoft.pydroid.ui.loader;

import android.support.annotation.CheckResult;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.pyamsoft.pydroid.function.ActionSingle;
import com.pyamsoft.pydroid.helper.Checker;
import com.pyamsoft.pydroid.ui.loader.targets.Target;

public abstract class GenericLoader<T> {

  @ColorRes private int tint;
  @Nullable private ActionSingle<Target<T>> startAction;
  @Nullable private ActionSingle<Target<T>> errorAction;
  @Nullable private ActionSingle<Target<T>> completeAction;

  protected GenericLoader() {
    tint = 0;
  }

  @NonNull public GenericLoader tint(@ColorRes int color) {
    this.tint = color;
    return this;
  }

  @NonNull public GenericLoader setStartAction(@NonNull ActionSingle<Target<T>> startAction) {
    this.startAction = Checker.checkNonNull(startAction);
    return this;
  }

  @NonNull public GenericLoader setErrorAction(@NonNull ActionSingle<Target<T>> errorAction) {
    this.errorAction = Checker.checkNonNull(errorAction);
    return this;
  }

  @NonNull public GenericLoader setCompleteAction(@NonNull ActionSingle<Target<T>> completeAction) {
    this.completeAction = Checker.checkNonNull(completeAction);
    return this;
  }

  @CheckResult int tint() {
    return tint;
  }

  @CheckResult @Nullable protected ActionSingle<Target<T>> startAction() {
    return startAction;
  }

  @CheckResult @Nullable protected ActionSingle<Target<T>> errorAction() {
    return errorAction;
  }

  @CheckResult @Nullable protected ActionSingle<Target<T>> completeAction() {
    return completeAction;
  }
}
