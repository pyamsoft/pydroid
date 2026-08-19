# pydroid-core
Core PYDroid library

## What is this

This is the core that all other PYDroid libraries are built on.

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
    implementation "com.github.pyamsoft.pydroid:core:<version>"
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
    implementation(libs.pydroid.core)
}
```

in `gradle/libs.versions.toml`
```toml
[versions]
pydroid = "<version>"

[libraries]
pydroid-core = { group = "com.github.pyamsoft.pydroid", name = "core", version.ref = "pydroid" }
```

## How to Use

Contains the core libraries for all PYDroid based projects, like Kotlin and the Android compat
annotations. Also contains the `Enforcer` class, which will throw an exception if a given context
is running on or off of the Main thread.
