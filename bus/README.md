# pydroid-bus
PYDroid Event Bus

## What is this

A simple event bus powered by coroutines

## Install

In your module's `build.gradle`:
```
dependencies {
    implementation "com.github.pyamsoft.pydroid:bus:<version>"
}
```

## How to Use

Create an `EventBus` using `EventBus.create()`. Publish events to it via `send`
and listen for events via `onEvent`
