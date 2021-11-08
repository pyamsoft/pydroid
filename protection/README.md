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
      includeGroup("com.github.pyamsoft.pydroid")
    }
  }
}

dependencies {
    implementation "com.github.pyamsoft.pydroid:protection:<version>"
}
```

## How to Use

Used internally in pydroid-ui, which is it's only supported usage.

