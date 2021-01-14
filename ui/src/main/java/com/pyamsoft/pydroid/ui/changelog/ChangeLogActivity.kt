/*
 * Copyright 2020 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.ui.changelog

import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.lifecycle.ViewModelProvider
import com.pyamsoft.pydroid.arch.StateSaver
import com.pyamsoft.pydroid.arch.createComponent
import com.pyamsoft.pydroid.ui.Injector
import com.pyamsoft.pydroid.ui.PYDroidComponent
import com.pyamsoft.pydroid.ui.arch.fromViewModelFactory
import com.pyamsoft.pydroid.ui.internal.changelog.ChangeLogControllerEvent
import com.pyamsoft.pydroid.ui.internal.changelog.ChangeLogProvider
import com.pyamsoft.pydroid.ui.internal.changelog.ChangeLogViewModel
import com.pyamsoft.pydroid.ui.internal.changelog.dialog.ChangeLogDialog
import com.pyamsoft.pydroid.ui.rating.RatingActivity

/**
 * An activity which displays an in-app changelog
 */
public abstract class ChangeLogActivity : RatingActivity(), ChangeLogProvider {

    private var stateSaver: StateSaver? = null

    internal var changeLogFactory: ViewModelProvider.Factory? = null
    private val viewModel by fromViewModelFactory<ChangeLogViewModel> { changeLogFactory }

    /**
     * Version name of application
     */
    protected abstract val versionName: String

    /**
     * On activity create
     */
    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Injector.obtainFromApplication<PYDroidComponent>(this)
            .plusChangeLog()
            .create()
            .inject(this)

        stateSaver = createComponent(
            savedInstanceState, this,
            viewModel
        ) {
            return@createComponent when (it) {
                is ChangeLogControllerEvent.LoadChangelog -> ChangeLogDialog.open(this)
            }
        }
    }

    /**
     * On save instance state
     */
    @CallSuper
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        stateSaver?.saveState(outState)
    }

    /**
     * On destroy
     */
    @CallSuper
    override fun onDestroy() {
        super.onDestroy()
        changeLogFactory = null
        stateSaver = null
    }

    /**
     * On post resume, we can show Dialogs
     */
    @CallSuper
    override fun onPostResume() {
        super.onPostResume()

        // Called in onPostResume so that the DialogFragment can be shown correctly.
        viewModel.show(false)
    }
}
