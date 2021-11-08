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
    implementation "com.github.pyamsoft.pydroid:loader:<version>"
}
```
