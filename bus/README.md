# pydroid-bus
PYDroid Event Bus

## What is this

A simple event bus powered by coroutines

## Install

In your module's `build.gradle`:
```groovy
repositories {
  // Jitpack
  maven {
    setUrl("https://jitpack.io")
    content {
      includeGroup("com.github.pyamsoft.pydroid")
      includeGroup("com.github.pyamsoft")
    }
  }
}

dependencies {
    implementation "com.github.pyamsoft.pydroid:bus:<version>"
}
```

OR the new way:

In your module's `build.gradle`:
```groovy
repositories {
  // Jitpack
  maven {
    setUrl("https://jitpack.io")
    content {
      includeGroup("com.github.pyamsoft.pydroid")
      includeGroup("com.github.pyamsoft")
    }
  }
}

dependencies {
    implementation(libs.pydroid.bus)
}
```

in `gradle/libs.versions.toml`
```toml
[versions]
pydroid = "<version>"

[libraries]
pydroid-bus = { group = "com.github.pyamsoft.pydroid", name = "bus", version.ref = "pydroid" }
```

## How to Use

Create an `EventBus<T>` using `DefaultEventBus<T>`. An `EventBus` is currently just
an interface alias to `SharedFlow<T>`, with it's implementation in `DefaultEventBus<T>`
being a `MutableSharedFlow<T>`
