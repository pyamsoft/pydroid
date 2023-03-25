/*
 * Copyright 2023 pyamsoft
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

package com.pyamsoft.pydroid.ui.widget.materialcheckable

import androidx.compose.runtime.Composable

/**
 * Given a list of MaterialCheckables in the same parent component, find the one with the largest
 * height.
 *
 * Provide back a Gap height which other components can use to size themselves to the same height as
 * the largest component.
 *
 * Will only work given that the largest component does not update it's height after being measured.
 */
public interface HeightMatcherGenerator<T : Any> {

  /** Generate a [HeightMatcher] for a given item */
  @Composable public fun generateFor(item: T): HeightMatcher
}
