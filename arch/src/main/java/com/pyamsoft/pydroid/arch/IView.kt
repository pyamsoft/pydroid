package com.pyamsoft.pydroid.arch

interface IView<S : UiViewState, V : UiViewEvent> : Renderable<S>, Inflatable<S>,
    Initializable, SaveableState
