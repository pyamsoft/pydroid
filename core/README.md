# pydroid-core
Core PYDroid library

## What is this

This is the core that all other PYDroid libraries are built on.

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
    implementation "com.github.pyamsoft.pydroid:core:<version>"
}
```

## How to Use

Contains the core libraries for all PYDroid based projects, like Kotlin and the Android compat
annotations. Also contains the `Enforcer` class, which will throw an exception if a given context
is running on or off of the Main thread.
