package com.pyamsoft.pydroid.arch

import androidx.annotation.CheckResult
import kotlinx.coroutines.CoroutineScope

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
     * Render a state
     */
    public fun render(scope: CoroutineScope, onRender: (state: S) -> Unit)
}

