/*
 * Copyright 2019 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.ui.arch

object InvalidUiComponentIdException : RuntimeException(
    """
      |The UiView which powers this UiComponent is in turn powered
      |by a PreferenceFragment from the AndroidX framework which
      |is a strange beast and does not fit into the UiComponent
      |architecture that the rest of the application has tried to
      |establish. This view has no id(), and to attempt to use it
      |is incorrect.
    """.trimMargin()
)
