# pydroid-ui
PYDroid reference UI implementation

## What is this

The reference UI implementation for PYDroid components

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
    implementation "com.github.pyamsoft.pydroid:ui:<version>"
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
    implementation(libs.pydroid.ui)
}
```

in `gradle/libs.versions.toml`
```toml
[versions]
pydroid = "<version>"

[libraries]
pydroid-ui = { group = "com.github.pyamsoft.pydroid", name = "ui", version.ref = "pydroid" }
```

## How to Use

Contains UI for:

- A basic "Open Source Libraries" screen  
- Play Store suggested app upgrades  
- Play Store app review suggestions  
- Change Log dialog display  
- In-App purchase dialog display  
- Confirmation dialog before navigating to External URI destinations  
- Application Settings

Entry points are designed for `Application` classes and your single or Main `Activity`
- Call `installPYDroid` from your `Application` class.
- Call `installPYDroid` (and optionally `installPYDroidExtras`) from your Main `Activity`
