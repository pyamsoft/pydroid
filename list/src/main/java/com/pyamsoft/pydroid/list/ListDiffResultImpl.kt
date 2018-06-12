/*
 * Copyright (C) 2018 Peter Kenji Yamanaka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pyamsoft.pydroid.list

import androidx.recyclerview.widget.DiffUtil.DiffResult
import com.pyamsoft.pydroid.list.ListDiffResult.ListData
import java.util.Collections

class ListDiffResultImpl<out T : Any>(
  list: List<T>,
  private val result: DiffResult
) : ListDiffResult<T> {

  private val list: List<T> = Collections.unmodifiableList(list)

  override fun ifEmpty(func: () -> Unit) {
    if (list.isEmpty()) {
      func()
    }
  }

  override fun withValues(func: (ListData<T>) -> Unit) {
    if (list.isNotEmpty()) {
      func(ListDataImpl(list, result))
    }
  }

  private class ListDataImpl<out T : Any>(
    list: List<T>,
    private val result: DiffResult
  ) : ListData<T> {

    private val list: List<T> = Collections.unmodifiableList(list)

    override fun list(): List<T> = list

    override fun dispatch(func: (DiffResult) -> Unit) {
      func(result)
    }

  }
}
