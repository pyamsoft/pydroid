# pydroid-theme
PYDroid MaterialTheme extensions

## What is this

Useful extensions to MaterialTheme

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
    implementation "com.github.pyamsoft.pydroid:theme:<version>"
}
```

## How to Use

See `MaterialTheme.keylines` and `LocalKeylines` for theme based spacing. See `SpacingDefaults` for default values.
