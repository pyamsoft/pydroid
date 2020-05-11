package com.pyamsoft.pydroid.ui.settings

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CheckResult
import androidx.appcompat.view.ContextThemeWrapper
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.pyamsoft.pydroid.arch.BaseUiView
import com.pyamsoft.pydroid.arch.StateSaver
import com.pyamsoft.pydroid.arch.createComponent
import com.pyamsoft.pydroid.ui.Injector
import com.pyamsoft.pydroid.ui.PYDroidComponent
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.arch.factory
import com.pyamsoft.pydroid.ui.databinding.LayoutConstraintBinding
import com.pyamsoft.pydroid.ui.util.commit
import com.pyamsoft.pydroid.ui.util.layout
import com.pyamsoft.pydroid.ui.widget.shadow.DropshadowView
import com.pyamsoft.pydroid.util.toDp
import timber.log.Timber

internal abstract class AppSettingsPopoutDialog protected constructor() : DialogFragment() {

    internal var toolbar: AppSettingsPopoutToolbar? = null

    internal var frame: AppSettingsPopoutFrame? = null

    internal var factory: ViewModelProvider.Factory? = null
    private val viewModel by factory<AppSettingsPopoutViewModel> { factory }

    private var stateSaver: StateSaver? = null

    private val themeFromAttrs: Int by lazy(LazyThreadSafetyMode.NONE) {
        valueFromCurrentTheme(R.attr.dialogTheme)
    }

    @CheckResult
    private fun valueFromCurrentTheme(attr: Int): Int {
        val context = requireActivity()
        val attributes = context.obtainStyledAttributes(intArrayOf(attr))
        val dimension = attributes.getResourceId(0, 0)
        attributes.recycle()
        return dimension
    }

    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.layout_constraint, container, false)

        val margin = 16.toDp(view.context)
        val params = view.layoutParams
        if (params == null) {
            view.layoutParams = ViewGroup.MarginLayoutParams(margin, margin * 2)
        } else {
            view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = margin
                bottomMargin = margin
                marginStart = margin
                marginEnd = margin
            }
        }

        return view
    }

    final override fun getTheme(): Int {
        return themeFromAttrs
    }

    final override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(ContextThemeWrapper(requireActivity(), theme), theme)
    }

    final override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        val binding = LayoutConstraintBinding.bind(view)
        Injector.obtain<PYDroidComponent>(view.context.applicationContext)
            .plusSettingsPopout()
            .create(getInitialTitle(), getToolbarBackground(), binding.layoutConstraint)
            .inject(this)

        val toolbar = requireNotNull(toolbar)
        val dropshadow =
            DropshadowView.createTyped<AppSettingsPopoutViewState, AppSettingsPopoutViewEvent>(
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
                is AppSettingsPopoutControllerEvent.ClosePopout -> requireActivity().onBackPressed()
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

    @CheckResult
    protected abstract fun getContents(): Fragment

    @CheckResult
    protected abstract fun getInitialTitle(): String

    @CheckResult
    protected open fun getToolbarBackground(): Drawable {
        return ColorDrawable(
            ContextCompat.getColor(
                requireActivity(),
                valueFromCurrentTheme(R.attr.colorPrimary)
            )
        )
    }

    final override fun onSaveInstanceState(outState: Bundle) {
        stateSaver?.saveState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        toolbar = null
        frame = null
        factory = null
        stateSaver = null
    }
}
