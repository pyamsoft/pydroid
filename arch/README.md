# pydroid-arch
Architecture framework for a UiComponent based, ViewModel driven, reactive MVI UI design pattern


## What is this

The strict MVI based architecture framework for PYDroid applications

## Install

In your module's `build.gradle`:
```
dependencies {
    implementation "com.pyamsoft.pydroid:arch:<version>"
}
```

## How to Use

### What.

First, the mindset. pydroid-arch is a strict MVI framework, meaning its job is to take whatever you
tell it to think of as `state` and provide it to one or more views. It is entirely written in Kotlin
and powered by coroutines. Basic understanding of other MVI frameworks like
`MvRx` or `React` will help.

There are two ways to use this library, one as a supplement to other existing code, and the other a
full on adoption of the mindset and architecture.  

#### Simple

The light weight supplementary way provides a generic View layer and ViewModel layer, via the
`Renderable` interface and the simple `UiStateViewModel` class. `Renderable` is just an interface
which, when given a state, will render it on screen. It requires that you implement a function
`render(UiViewState) -> Unit` which is called EVERY time that a `UiStateViewModel` changes its held
`state` via `setState(UiViewState) -> UiViewState`. This means that your UI is always rendered as
a function of the state held by your `UiStateViewModel`. View states can be rebuilt entirely by
crafting a `state` in your `UiStateViewModel` and then passing it to your `Renderable` to render.

The `UiStateViewModel` is a JetPack view model
which has been extended to easily handle the management of a `state`. The view model controls the
understanding of `state` via the `UiViewState` interface. This `state` can only be accessed using
the functions `setState(UiViewState) -> UiViewState` which provides you with the current `state`
and requires you to modify and return a new `state`, and `withState(UiViewState) -> UiViewState`
which only provides the state to you but does not allow for modification.
`state` is not required to be immutable, but you really are shooting yourself in the foot if you
do not follow the immutable state recommendation that so many MVI frameworks propose -
pydroid-arch suggests that you make all of your `UiViewState` as Kotlin `data classes`. Like other
frameworks, accessing and mutating state are not synchronous operations - they will not happen
immediately, but will happen as soon as possible.

Since `Renderable` is just an interface, anything can implement it. For a `UiStateViewModel` you
may need to either provide the required `initialState` and `debug` parameters in the constructor, or
use a dependency injection solution with a custom ViewModel Factory.

While the simple lightweight adoption style will always be fully supported as a first class citizen,
the pydroid reference libraries are all developed following the opinionated full-adoption style.

#### Full

The full adoption way is more opinionated.

pydroid-arch wants you to think of your code as modular `Components`. A component is a grouping of
a `UiViewModel`, one or more `UiViews`, and a `UiController`. 

The `UiViewModel` is the `UiStateViewModel` class, extended with some small extras.
The UiViewModel is the middleman between a View and a Controller - and requires you to override a
function called `handleViewEvent(UiViewEvent) -> Unit` which is called in response to any event
fired from any UiView. The UiViewModel also provides a `publish(UiControllerEvent) -> Unit`
function, which can be called to pass events to the Controller.

The `UiView` class is NOT Android's representation of a View. The `UiView` is a `Renderable` and can
be one or more views in a logical grouping, such as a RecyclerView and FloatingActionButton, or
TabLayout and ViewPager. The `UiView` provides a `publish(UiViewEvent) -> Unit` function which will
pass events to the `UiViewModel`. 

The `UiController` has only one job, which is to synchronously handle `UiControllerEvents`. These
events are published by the `UiViewModel` and are generally one-off operations which do not affect
what is rendered on screen, such as navigation or alerts.

These three pieces come together to make a component via
`createComponent(Bundle?, LifecycleOwner, UiViewModel, vararg UiViewState, (UiControllerEvent) -> Unit) -> StateSaver`.
It takes a saved instance state bundle, a lifecycle owner, and then your component members.
What it returns for use is a `StateSaver` class, which has one function called `saveState(Bundle)`
which takes a `Bundle` and will save any requested state bits to it.
For most expected setups, `createComponent` is called in `Activity.onCreate` or
`Fragment.onViewCreated` and the `StateSaver.saveState(Bundle)` is called in
`onSaveInstanceState(Bundle)`. This means that, while not required, pydroid-arch generally
recommends that you treat your Android Activities and Fragments as component controllers.

For `ListView` and `RecyclerView` `ViewHolder` items, pydroid-arch provides a simpler component
model using `bindViews(LifecycleOwner, vararg UiViewState, (UiViewEvent) -> Unit) -> ViewBinder`.
Because list items are meant to be simple, they do not bind a `UiViewModel` or
handle `UiControllerEvents`. They are expected handle view events only, and most always will do so
by passing the event up to the ListView or RecyclerView itself, which will have its own
`UiViewModel` and `UiControllerEvent` handler. They also do not need to save or restore from
`Bundle` state. The call to `bindViews` returns a ViewBinder, which has just one
method - `bind(UiViewState)` - which you can call in `onBindViewHolder` to bind your state
to your items.

It sounds like a lot, and to be fair, it is - but hey - its Android. Just remember - your
`UiViews` are modular. They can either be grouped into a `Component` with a `UiViewModel`
and a `Controller` or be bound into list items. Remember to save state and restore properly.

##### Hooks

Hooks are provided for `UiView` and `UiViewModel` classes to run code on certain
lifecycle events. The `doOnRestoreState { Bundle? -> }` hook will be run whenever the savedInstanceState
is restored for the `UiViewModel`. `doOnCleared` is called when the ViewModel is being cleared.

For views, the `doOnInflate { Bundle? -> }` acts similarly when the view is first inflated.

The `doOnTeardown { }` hook will be run when when the object is being destroyed - either the view is
leaving the screen or the view model is falling out of lifecycle scope.

The `doOnSaveState { Bundle -> }` hook will be run when a `StateSaver` calls
`saveState(Bundle)`, usually in `onSaveInstanceState` in an Activity or Fragment.

You can use as many hooks as you want - they will never override each other.
There is no way to peek or remove queued hooks though, so be sure that you have any conditional
running logic in your hook itself.

Any hooks added after the runpoint in the lifecycle will not be executed.
