# pydroid-inject
PYDroid Injector

## What is this

The reference Injector to access a service locator

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
    implementation "com.github.pyamsoft.pydroid:inject:<version>"
}
```

## How to Use

```kotlin
Injector.obtainFromApplication<MyGraph>(Context)
Injector.obtainFromActivity<MyGraph>(Context)
Injector.obtainFromService<MyGraph>(Context)
```
