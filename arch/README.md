# pydroid-arch

PYDroid standard architecture for an MVVM UI design pattern

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

PYDroid-Arch is an MVVM framework that is built for Jetpack Compose first applications.
The idea is split into 3 parts, the `UiViewState`, `ViewModeler`, and `SaveStateDisposableEffect`.

First, the `UiViewState`. `UiViewState` is a simple interface with zero contract requirements,
though the reference implementations will exclusively expose `StateFlow<*>` fields in a `UiViewState`
interface. The reference implementation exposes individual `StateFlow<*>` per state variable instead
of the MVI style of representing the whole state as a single flow to avoid Compose recompositions
in Composables which consume only individual fields of the `UiViewState`.

Second, the `ViewModeler`. `ViewModeler` classes are just like the `AndroidX ViewModel`, but without the
weird Factory requirement. We break away from `AndroidX ViewModel` because it has a bunch of code in
the Factory so that it can handle configuration changes, but seeing as Compose handles configuration
changes itself, we don't need to whole ceremony around ViewModel providers and factories, though you
can easily create your own implementation of a `ViewModeler` backed by an `AndroidX ViewModel`.
The default implementation of `ViewModeler` is the `AbstractViewModeler`, which expects to be passed a
`MutableUiViewState` implementation.

A `MutableUiViewState` implementation is a `UiViewState` class which exposes `MutableStateFlow<*>` fields,
as this keeps the `UiViewState` contract of exposing `StateFlow<*>` fields, but also allows a `ViewModeler`
implementation to mutate the flows. This keeps mutation of data exclusive to `ViewModeler` classes,
and the state can then be consumed in composables by using the standard
`val data by state.data.collectAsState()` extension.

Finally, the `SaveStateDisposableEffect`. This effect connects the `ViewModeler` passed to it to the
Compose instance state restoration hooks, and allows a `ViewModeler` to register and consumed saved
instance state.

Sharing state in this way is simple because you only need to keep the `MutableUiViewState` at the share level,
you can create as many ViewModeler instances as you want, as long as they are all backed by the same
`MutableUiViewState` instance.

A minimal implementation is as follows:
```kotlin

class MyViewModeler(
  override val state: MutableMyViewState,
): MyViewState by state, AbstractViewModeler<MyViewState>(state) {

  // We inherit from the base ViewModeler implementation.
  // For convenience, we delegate the ViewModeler to be the
  // readonly contract for MyViewState

  fun handleUpdateSomeData(someData: SomeData) {
    // State can only be mutated from inside the ViewModeler via the 
    // state member variable
    state.someData.value = someData
  }

}

@Composable
fun MyEntryPoint(
  modifier: Modifier = Modifier,
  // Inject or get the ViewModeler however you want
  viewModeler: MyViewModeler,
) {
  MyComposable(
    modifier = modifier,
    state = viewModeler,
    onUpdateSomeData = { viewModeler.handleUpdateSomeData(it) },
  )
}

@Composable
private fun MyComposable(
  modifier: Modifier = Modifier,
  state: MyViewState,
  onUpdateSomeData: (SomeData) -> Unit,
) {
  val someData by state.someData.collectAsState()
  val otherData by state.otherData.collectAsState()

  RenderSomeData(
    someData = someData,
    onUpdateSomeData = onUpdateSomeData,
  )
  RenderOtherData(
    otherData = otherData,
  )
}
```

You can take a look at the reference architecture implementation inside of the
[bootstrap](https://github.com/pyamsoft/pydroid/tree/main/bootstrap) and
[ui](https://github.com/pyamsoft/pydroid/tree/main/ui) modules.
