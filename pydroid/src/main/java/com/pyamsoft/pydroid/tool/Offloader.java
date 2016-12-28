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

package com.pyamsoft.pydroid.tool;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.pyamsoft.pydroid.ActionNone;
import com.pyamsoft.pydroid.ActionSingle;
import com.pyamsoft.pydroid.FuncNone;

/**
 * A class which can help with offloading work to the onProcess
 *
 * Cancel does not guarantee immediate cancelling, do not rely too heavily on this class, think of
 * it more as a way to abstract hard dependency on AsyncTask out of code base
 *
 * Requires a onProcess function, but onFinish and onError handling are optional
 */
public interface Offloader<T> {

  @CheckResult @NonNull Offloader<T> onProcess(@NonNull FuncNone<T> background);

  @CheckResult @NonNull Offloader<T> onError(@NonNull ActionSingle<Throwable> error);

  @CheckResult @NonNull Offloader<T> onResult(@NonNull ActionSingle<T> result);

  @CheckResult @NonNull Offloader<T> onFinish(@NonNull ActionNone finisher);

  @CheckResult @NonNull ExecutedOffloader execute();
}
