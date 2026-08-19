# pydroid-billing
PYDroid In-App Billing

## What is this

A simple abstraction of Android In-App Billing. By default this module only contains interfaces and abstract
implementations that simplify billing. You ususally do NOT want this library module directly, but should instead
pull in one of it's concrete implementations
[billing-noop](https://github.com/pyamsoft/pydroid/tree/main/billing-noop)
or
[billing-play](https://github.com/pyamsoft/pydroid/tree/main/billing-play)

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
    implementation "com.github.pyamsoft.pydroid:billing:<version>"
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
    implementation(libs.pydroid.billing)
}
```

in `gradle/libs.versions.toml`
```toml
[versions]
pydroid = "<version>"

[libraries]
pydroid-billing = { group = "com.github.pyamsoft.pydroid", name = "billing", version.ref = "pydroid" }
```

## How to Use

You ususally do NOT want this library module directly, but should instead
pull in one of it's concrete implementations
[billing-noop](https://github.com/pyamsoft/pydroid/tree/main/billing-noop)
or
[billing-play](https://github.com/pyamsoft/pydroid/tree/main/billing-play)
