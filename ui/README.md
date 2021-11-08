# pydroid-ui
PYDroid reference UI implementation

## What is this

The reference UI implementation for PYDroid components

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
    implementation "com.github.pyamsoft.pydroid:ui:<version>"
}
```

## How to Use

Contains UI for:

A basic "Open Source Libraries" screen
Play Store suggested app upgrades
Play Store app review suggestions
Application Settings
An "Other Applications by developer" upsell - marked as advertisement
