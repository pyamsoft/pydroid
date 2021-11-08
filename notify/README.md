# pydroid-notify
PYDroid Notification management system

## What is this

A simple scalable notification system.

## Install

In your module's `build.gradle`:
```
repositories {

  maven {
    url 'https://jitpack.io'
    content {
      includeGroup("com.github.pyamsoft.pydroid")
    }
  }
}

dependencies {
    implementation "com.github.pyamsoft.pydroid:notify:<version>"
}
```

## How to Use

Notifications are dealt with via a `Notifier` instance, which knows of one or more
`NotifyDispatchers`. A `NotifyDispatcher` has a validation function to check if
the given dispatcher can handle a request to post a notification, as well as an acting function
to take a payload of information and turn it into a native system Notification.

A `DefaultNotifier` is provided for convenience.
