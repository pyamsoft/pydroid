# pydroid-protection
PYDroid application protection

## What is this

Secure applications from bad actors.

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
    implementation "com.github.pyamsoft.pydroid:protection:<version>"
}
```

## How to Use

Used internally in pydroid-ui, which is it's only supported usage.

