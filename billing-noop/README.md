# pydroid-billing-play
PYDroid In-App Billing (Stub/No-Op)

## What is this

A stub/no-op implementation of pyamsoft In-App Billing that does nothing.
The only other implementation is Google Play In-App Billing backed.
Use this implementation when you do NOT want to rely on Google libraries.

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
    implementation "com.github.pyamsoft.pydroid:billing-noop:<version>"
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
    implementation(libs.pydroid.billing.noop)
}
```

in `gradle/libs.versions.toml`
```toml
[versions]
pydroid = "<version>"

[libraries]
pydroid-billing-noop = { group = "com.github.pyamsoft.pydroid", name = "billing-noop", version.ref = "pydroid" }
```

## How to Use

Entry point is the `NoopBillingModule` class, which exposes a `BillingConnector`.
