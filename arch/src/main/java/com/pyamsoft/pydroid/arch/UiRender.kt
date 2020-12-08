package com.pyamsoft.pydroid.arch

import androidx.annotation.CheckResult

/**
 * Represents an interface which can observe a UiViewState for render events
 */
public interface UiRender<S> {

    /**
     * Unique stream by the specific piece of a state
     */
    @CheckResult
    public fun <T> distinctBy(distinctBy: (state: S) -> T): UiRender<T>

    /**
     * Unique stream by the specific piece of a state having changed from old to new
     */
    @CheckResult
    public fun distinct(areEquivalent: (old: S, new: S) -> Boolean): UiRender<S>

    /**
     * Render a state
     */
    public fun render(onRender: (state: S) -> Unit)

}

