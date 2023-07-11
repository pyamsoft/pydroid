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
      includeGroup("com.github.pyamsoft.pydroid")
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

- A basic "Open Source Libraries" screen  
- Play Store suggested app upgrades  
- Play Store app review suggestions  
- Change Log dialog display  
- In-App purchase dialog display  
- Confirmation dialog before navigating to External URI destinations  
- Application Settings  
