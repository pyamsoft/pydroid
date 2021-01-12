package com.pyamsoft.pydroid.arch

import androidx.annotation.CheckResult
import kotlinx.coroutines.CoroutineScope

/**
 * Represents an interface which can observe a UiViewState for render events
 */
public interface UiRender<S> {

    /**
     * Unique stream by the specific piece of a state
     *
     * The stream will emit only when the value T which is derived from state S changes.
     * This maps the resulting stream into a stream of T
     */
    @CheckResult
    @Deprecated("Use onChanged", replaceWith = ReplaceWith("onChanged(distinctBy)"))
    public fun <T> distinctBy(distinctBy: (state: S) -> T): UiRender<T>

    /**
     * Unique stream by the specific piece of a state
     *
     * The stream will emit only when the value T which is derived from state S changes.
     * This maps the resulting stream into a stream of T
     */
    @CheckResult
    public fun <T> mapChanged(change: (state: S) -> T): UiRender<T>

    /**
     * Render a state
     */
    public fun render(scope: CoroutineScope, onRender: (state: S) -> Unit)
}

