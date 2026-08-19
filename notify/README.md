# pydroid-notify
PYDroid Notification management system

## What is this

A simple scalable notification system.

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
    implementation "com.github.pyamsoft.pydroid:notify:<version>"
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
    implementation(libs.pydroid.notify)
}
```

in `gradle/libs.versions.toml`
```toml
[versions]
pydroid = "<version>"

[libraries]
pydroid-notify = { group = "com.github.pyamsoft.pydroid", name = "notify", version.ref = "pydroid" }
```

## How to Use

Notifications are dealt with via a `Notifier` instance, which knows of one or more
`NotifyDispatchers`. A `NotifyDispatcher` has a validation function to check if
the given dispatcher can handle a request to post a notification, as well as an acting function
to take a payload of information and turn it into a native system Notification.

A `DefaultNotifier` is provided for convenience.
