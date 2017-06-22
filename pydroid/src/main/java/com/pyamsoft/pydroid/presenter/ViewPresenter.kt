/*
 * Copyright 2017 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.presenter

import android.view.View
import android.widget.CompoundButton
import com.pyamsoft.pydroid.rx.RxViews

abstract class ViewPresenter : Presenter(), ViewPresenterContract {

  final override fun clickEvent(view: View, func: (View) -> Unit) {
    disposeOnStop {
      RxViews.onClick(view).subscribe {
        func(it)
      }
    }
  }

  final override fun checkChangedEvent(view: CompoundButton,
      func: (CompoundButton, Boolean) -> Unit) {
    disposeOnStop {
      RxViews.onCheckChanged(view).subscribe {
        func(it.view, it.checked)
      }
    }
  }
}

