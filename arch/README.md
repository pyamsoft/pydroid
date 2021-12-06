# pydroid-arch
Architecture framework for a UiComponent based, ViewModel driven, reactive MVI UI design pattern


## What is this

The strict MVI based architecture framework for PYDroid applications

## Install

In your module's `build.gradle`:
```groovy
repositories {

  maven {
    url 'https://jitpack.io'
    content {
      includeGroup("com.github.pyamsoft.pydroid")
      includeGroup("com.github.pyamsoft")
    }
  }
}

dependencies {
    implementation "com.github.pyamsoft.pydroid:arch:<version>"
}
```

## How to Use

### What

First, the mindset. pydroid-arch is a strict MVI framework, meaning its job is to take whatever you
tell it to think of as `state` and provide it to one or more views. It is entirely written in Kotlin
and powered by coroutines. Basic understanding of other MVI frameworks like `MvRx` or `React` will help.

### How

Usage is simple, and you can adopt some or all of PYDroid-Arch when building your applications.
PYDroid-Arch works using the mental model of a `Component`, which are three separate pieces connected
together. These pieces are the `UiView`, the `UiViewModel` and the `UiController`.

Let's start at the top. The `User` interacts with your application. They send their interactions to
the stuff on screen - which are `UiView` objects. A `UiView` is a simple class which implements the
`Renderable` interface and knows how to draw a given `UiViewState`. A `UiView` class can be anything,
just because the name contains the word View does not mean that a `UiView` must be an Android View.
In fact, in PYDroid, `UiView` objects are usually a collection of related Android Views, such as a
group of buttons, or a list of items. `UiViews` are able to render `UiViewState` payloads into content
on the screen, and they are able to publish `UiViewEvents` to the `UiViewModel`. These `UiViewEvents`
tell the `UiViewModel` about what actions the user has taken when interacting with the application.

The `UiViewModel` manages and updates the model which determines what the current `UiViewState`
represents. A `UiViewModel` can only hold one single `UiViewState` object, but that state can be as
complex or as simple as you need it to be. A `UiViewModel` is able to retrieve the current `state`
immediately with a synchronous operation, or can mutate the existing state via an asynchronous
`setState` method which updates the entire `state` at once atomically. This helps guarantee that the
`UiViewState` which is modeled is always consistent. A `UiViewModel` can receive `UiViewEvents` from
the `UiView`, by calling
`UiViewModel.bindViews(CoroutineScope, UiSavedStateReader, Array<UiView<S, V>>, onViewEvent: (V) -> Unit)`.
This method binds a `UiViewModel` to one or more `UiView` objects, and will receive their `UiViewEvent`
data when it is published by the view. It can then react to those events via the `onViewEvent`
callback, and respond to user interactions. The `UiViewModel` can also drive the navigation of the
application by publishing `UiControllerEvent` payloads. The `UiControllerEvent` is handled by a
`UiController`, which usually responds by navigating to a different screen or showing a dialog. A
`UiViewModel` can bind itself to a `UiController` via the
`UiViewModel.bindController(CoroutineScope, UiController<V>)` method. This method binds a `UiViewModel`
to a single `UiController`, which allows the controller to receive `UiControllerEvents` published by
the `UiViewModel` and react to them accordingly.

The `UiController` is the piece of the component which handles events outside of the current screen,
such as navigation and dialogs. These are generally one-off events and are not permanently represented
in the view state of the current screen. PYDroid generally thinks of Android's `Activity` and `Fragment`
classes as `UiController` classes.

### Getting Started

You can use PYDroid-Arch however you like. Nothing is stopping you from constructing your `UiView`
objects and a `UiViewModel` and binding them together. Nothing is stopping you from connecting a
`UiViewModel` to a `UiController`. But if you want a slightly easier way, and the way PYDroid uses
internally to implement it's UI, you're looking for `Components`.

To simplify the creation of these `Components`, library consumers can call the
`createComponent(Bundle?, LifecycleOwner, UiViewModel<S, C>, UiController<C>, Array<UiView<S, V>>, onViewEvent: (V) -> Unit)`
method. This is a convenience method which takes all of the inputs, and sets up a `Component` by
binding the `UiViewModel` to the provided `UiController` and associated `UiView` objects, and registers
it to the current `LifecycleOwner`, automatically cleaning itself up when the lifecycle ends.
This function is generally used in the `onCreate` of `Activity` or `Fragment` classes to enter the
`Component` world from the Android world. PYDroid recommends you set up `Components` as soon as
possible, in `Activity` classes this is in `onCreate`, and in `Fragment` classes, this is in
`onViewCreated`. In `Fragment` classes, PYDroid recommends the use of the `viewLifecycleOwner` when
a `LifecycleOwner` class is needed, assuming the `Fragment` class has an associated view.

For `ListView` and `RecyclerView` items which use a `ViewHolder` style, the method
`createViewBinder(Array<UiView<S, V>>, onViewEvent: (V) -> Unit)` exists, which can simplify binding
one or more `UiView` objects to a `ViewHolder` instance. This allows you to reap the same benefits
of the `UiView` structure, while delegating the handling of `UiViewEvent` payloads up to the
`UiController` which owns the actual list parent.

To get a working `UiViewModel` instance, PYDroid recommends some form of dependency injection.
```kotlin
class MyActivity : AppCompatActivity(), UiController<MyEvent> {

    var factory: ViewModelProvider.Factory? = null
    val viewModel by viewModels<MyViewModel> { factory.requireNotNull() }

    var stateSaver: StateSaver? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
      // ...
      stateSaver = createComponent(
        savedInstanceState,
        owner = this,
        viewModel = viewModel,
        controller = this,
        MyView1(),
        MyView2()
      ) { viewEvent ->
        return@createComponent when (viewEevnt) {
          // ...
        }
      }
    }

    override fun onSaveInstanceState(outState: Bundle) {
      // ...
      stateSaver?.saveState(outState)
    }

}

```

By providing a `ViewModelProvider.Factory` which knows how to construct your `MyViewModel` class,
the `fromViewModelFactory` extension function can help you initialize your `UiViewModel` lazily.

To get a `UiViewModel` which also understands the AndroidX `SavedStateHandle`, you can use
the `UiSavedStateViewModelProvider<*>` class along with the `asFactory`
extension function.

```kotlin
class MyActivity : AppCompatActivity(), UiController<MyEvent> {

    var provider: UiSavedStateViewModelProvider<MySavedStateViewModel>? = null
    val viewModel by viewModels<MySavedStateViewModel> {
      provider.requireNotNull().asFactory(this)
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
      // ...
    }
}

```
