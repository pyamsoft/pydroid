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

package com.pyamsoft.pydroid.ui.about.dialog

import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintSet
import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pydroid.arch.BaseUiComponent
import com.pyamsoft.pydroid.arch.doOnDestroy
import com.pyamsoft.pydroid.ui.about.dialog.UrlToolbarUiComponent.Callback
import com.pyamsoft.pydroid.ui.widget.shadow.DropshadowView

internal class UrlToolbarUiComponentImpl internal constructor(
  private val toolbar: UrlToolbarView,
  private val dropshadow: DropshadowView,
  private val binder: UrlToolbarBinder
) : BaseUiComponent<UrlToolbarUiComponent.Callback>(),
    UrlToolbarUiComponent,
    UrlToolbarBinder.Callback {

  override fun id(): Int {
    return toolbar.id()
  }

  override fun onBind(
    owner: LifecycleOwner,
    savedInstanceState: Bundle?,
    callback: Callback
  ) {
    owner.doOnDestroy {
      toolbar.teardown()
      dropshadow.teardown()
      binder.unbind()
    }

    toolbar.inflate(savedInstanceState)
    dropshadow.inflate(savedInstanceState)
    binder.bind(this)
  }

  override fun onLayout(set: ConstraintSet) {
    dropshadow.also {
      set.connect(it.id(), ConstraintSet.TOP, toolbar.id(), ConstraintSet.BOTTOM)
      set.connect(it.id(), ConstraintSet.START, toolbar.id(), ConstraintSet.START)
      set.connect(it.id(), ConstraintSet.END, toolbar.id(), ConstraintSet.END)
      set.constrainWidth(it.id(), ConstraintSet.MATCH_CONSTRAINT)
    }
  }

  override fun onSaveState(outState: Bundle) {
    toolbar.saveState(outState)
    dropshadow.saveState(outState)
  }

  override fun handleNavigationEvent() {
    callback.onCancelViewing()
  }

  override fun handleViewLicenseExternal(url: String) {
    callback.onNavigateToExternalUrl(url)
  }

}