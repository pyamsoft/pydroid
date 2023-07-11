# pydroid-bootstrap
Bootstrap an application quickly with common requirements
like version checking and library attribution.

## What is this

The data layers for bootstrapping a new application quickly

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
    implementation "com.github.pyamsoft.pydroid:bootstrap:<version>"
}
```

## How to Use

pydroid-bootstrap includes pre-built data layer logic for creating:

- An "About This Application" or "Open Source Libraries" screen
- Play Store app upgrade suggestions
- Play Store app review suggestions

This module only provides the data models and use case layer. It does not provide presentation
logic or UI on its own.
