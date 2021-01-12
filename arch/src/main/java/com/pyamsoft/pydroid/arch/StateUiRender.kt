package com.pyamsoft.pydroid.arch

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * A UiRender for a given snapshot of state.
 *
 * Used for Android view compatibility
 */
private class StateUiRender<S>(private val state: S) : UiRender<S> {

    override fun render(scope: CoroutineScope, onRender: (state: S) -> Unit) {
        scope.launch(context = Dispatchers.Main) {
            onRender(state)
        }
    }

    override fun <T> mapChanged(change: (state: S) -> T): UiRender<T> {
        return StateUiRender(change(state))
    }

    override fun <T> distinctBy(distinctBy: (state: S) -> T): UiRender<T> {
        return mapChanged(distinctBy)
    }
}

/**
 * Convert data into a UiRender<S>
 */
public fun <S : UiViewState> S.asUiRender(): UiRender<S> {
    return StateUiRender(this)
}