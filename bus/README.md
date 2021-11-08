# pydroid-bus
PYDroid Event Bus

## What is this

A simple event bus powered by coroutines

## Install

In your module's `build.gradle`:
```groovy
repositories {

  maven {
    url 'https://jitpack.io'
    content {
      // PYDroid
      includeGroup("com.github.pyamsoft.pydroid")

      // Needed by pydroid-protection
      includeGroup("com.github.javiersantos")

      // pyamsoft Cachify and Highlander
      includeGroup("com.github.pyamsoft")
    }
  }
}

dependencies {
    implementation "com.github.pyamsoft.pydroid:bus:<version>"
}
```

## How to Use

Create an `EventBus` using `EventBus.create()`. Publish events to it via `send`
and listen for events via `onEvent`
