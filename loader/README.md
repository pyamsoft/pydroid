# pydroid-loader
Image loader abstraction for PYDroid components

## What is this

An image loader abstraction with a default Glide implementation

## Install

In your module's `build.gradle`:
```groovy
repositories {

  maven {
    url 'https://jitpack.io'
    content {
      includeGroup("com.github.pyamsoft.pydroid")
    }
  }
}

dependencies {
    implementation "com.github.pyamsoft.pydroid:loader:<version>"
}
```
