# pydroid-autopsy
PYDroid Crash Reporter screen

## What is this

A simple crash reporter screen which displays the stack trace.

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
    debugImplementation "com.github.pyamsoft.pydroid:autopsy:<version>"
}
```

## How to Use

Useful for when you are running a debug application on a real device and are not connected
to a crash reporting service or the device's logcat.

Uses a custom `ContentProvider` to install itself, so this dependency should only be requested in
`debug` builds.
