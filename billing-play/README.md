# pydroid-billing-play
PYDroid In-App Billing (Google Play)

## What is this

A simple implementation of pyamsoft In-App Billing backed by the Google Play In-App Billing library

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
    implementation "com.github.pyamsoft.pydroid:billing-play:<version>"
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
    implementation(libs.pydroid.billing.play)
}
```

in `gradle/libs.versions.toml`
```toml
[versions]
pydroid = "<version>"

[libraries]
pydroid-billing-play = { group = "com.github.pyamsoft.pydroid", name = "billing-play", version.ref = "pydroid" }
```

## How to Use

Entry point is the `PlayBillingModule` class, which exposes a `BillingConnector`.
