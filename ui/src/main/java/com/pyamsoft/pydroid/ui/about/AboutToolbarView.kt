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

package com.pyamsoft.pydroid.ui.about

import android.os.Bundle
import com.pyamsoft.pydroid.arch.UiView
import com.pyamsoft.pydroid.ui.about.AboutToolbarViewEvent.UpNavigate
import com.pyamsoft.pydroid.ui.app.ToolbarActivity
import com.pyamsoft.pydroid.ui.arch.InvalidIdException
import com.pyamsoft.pydroid.ui.util.DebouncedOnClickListener
import com.pyamsoft.pydroid.ui.util.setUpEnabled

internal class AboutToolbarView internal constructor(
  private val backstackCount: Int,
  private val toolbarActivity: ToolbarActivity
) : UiView<AboutToolbarState, AboutToolbarViewEvent>() {

  private var oldTitle: CharSequence? = null

  override fun id(): Int {
    throw InvalidIdException
  }

  override fun inflate(savedInstanceState: Bundle?) {
    if (savedInstanceState != null) {
      oldTitle = savedInstanceState.getCharSequence(KEY_OLD_TITLE)
    }

    toolbarActivity.withToolbar { toolbar ->
      if (oldTitle == null) {
        oldTitle = toolbar.title
      }

      toolbar.setUpEnabled(true)
      toolbar.setNavigationOnClickListener(DebouncedOnClickListener.create { publish(UpNavigate) })
    }
  }

  override fun render(
    state: AboutToolbarState,
    oldState: AboutToolbarState?
  ) {
    toolbarActivity.withToolbar { toolbar ->
      toolbar.title = state.title
    }
  }

  override fun teardown() {
    toolbarActivity.withToolbar { toolbar ->
      // Set title back to original
      toolbar.title = oldTitle ?: toolbar.title

      // If this page was last on the back stack, set up false
      if (backstackCount == 0) {
        toolbar.setUpEnabled(false)
      }

      toolbar.setNavigationOnClickListener(null)
    }
  }

  override fun saveState(outState: Bundle) {
    outState.putCharSequence(KEY_OLD_TITLE, oldTitle)
  }

  companion object {

    private const val KEY_OLD_TITLE = "key_old_title"
  }

}
