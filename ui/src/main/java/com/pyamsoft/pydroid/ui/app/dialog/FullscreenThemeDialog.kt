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

package com.pyamsoft.pydroid.ui.app.dialog

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CheckResult
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.pyamsoft.pydroid.arch.BaseUiView
import com.pyamsoft.pydroid.arch.StateSaver
import com.pyamsoft.pydroid.arch.createComponent
import com.pyamsoft.pydroid.ui.Injector
import com.pyamsoft.pydroid.ui.PYDroidComponent
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.app.dialog.ThemeDialogControllerEvent.Close
import com.pyamsoft.pydroid.ui.arch.viewModelFactory
import com.pyamsoft.pydroid.ui.databinding.LayoutConstraintBinding
import com.pyamsoft.pydroid.ui.util.commit
import com.pyamsoft.pydroid.ui.util.layout
import com.pyamsoft.pydroid.ui.widget.shadow.DropshadowView
import com.pyamsoft.pydroid.util.valueFromCurrentTheme
import timber.log.Timber
import com.google.android.material.R as R2

internal abstract class FullscreenThemeDialog protected constructor() : DialogFragment() {

    internal var toolbar: ThemeDialogToolbar? = null

    internal var frame: ThemeDialogFrame? = null

    internal var factory: ViewModelProvider.Factory? = null
    private val viewModel by viewModelFactory<ThemeDialogViewModel> { factory }

    private var stateSaver: StateSaver? = null

    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.layout_constraint, container, false)
    }

    final override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        val binding = LayoutConstraintBinding.bind(view)
        Injector.obtain<PYDroidComponent>(view.context.applicationContext)
            .plusThemeDialog()
            .create(getInitialTitle(), getToolbarBackground(), binding.layoutConstraint)
            .inject(this)

        val toolbar = requireNotNull(toolbar)
        val dropshadow =
            DropshadowView.createTyped<ThemeDialogViewState, ThemeDialogViewEvent>(
                binding.layoutConstraint
            )
        val frame = requireNotNull(frame)
        stateSaver = createComponent(
            savedInstanceState, viewLifecycleOwner,
            viewModel,
            frame,
            toolbar,
            dropshadow
        ) {
            Timber.d("Controller event: $it")
            return@createComponent when (it) {
                is Close -> dismiss()
            }
        }

        binding.layoutConstraint.layout {
            toolbar.also {
                connect(it.id(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
                connect(it.id(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
                connect(it.id(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
                constrainWidth(it.id(), ConstraintSet.MATCH_CONSTRAINT)
            }

            dropshadow.also {
                connect(it.id(), ConstraintSet.TOP, toolbar.id(), ConstraintSet.BOTTOM)
                connect(it.id(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
                connect(it.id(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
                constrainWidth(it.id(), ConstraintSet.MATCH_CONSTRAINT)
            }

            frame.also {
                connect(it.id(), ConstraintSet.TOP, toolbar.id(), ConstraintSet.BOTTOM)
                connect(it.id(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
                connect(it.id(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
                connect(
                    it.id(),
                    ConstraintSet.BOTTOM,
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.BOTTOM
                )
                constrainWidth(it.id(), ConstraintSet.MATCH_CONSTRAINT)
                constrainHeight(it.id(), ConstraintSet.MATCH_CONSTRAINT)
            }
        }

        pushContents(frame)
    }

    final override fun onResume() {
        super.onResume()
        dialog?.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setGravity(Gravity.CENTER)
        }
    }

    private fun pushContents(container: BaseUiView<*, *, *>) {
        val fm = childFragmentManager
        val tag = getInitialTitle()
        if (fm.findFragmentByTag(tag) == null) {
            fm.commit(viewLifecycleOwner) {
                add(container.id(), getContents(), tag)
            }
        }
    }

    final override fun onSaveInstanceState(outState: Bundle) {
        stateSaver?.saveState(outState)
        super.onSaveInstanceState(outState)
    }

    final override fun onDestroyView() {
        super.onDestroyView()
        toolbar = null
        frame = null
        factory = null
        stateSaver = null
    }

    @CheckResult
    protected abstract fun getContents(): Fragment

    @CheckResult
    protected abstract fun getInitialTitle(): String

    @CheckResult
    protected open fun getToolbarBackground(): Drawable {
        return ColorDrawable(
            ContextCompat.getColor(
                requireActivity(),
                requireActivity().valueFromCurrentTheme(R2.attr.colorPrimary)
            )
        )
    }
}
