# pydroid-billing
PYDroid In-App Billing

## What is this

A simple implementation of Android In-App Billing

## Install

In your module's `build.gradle`:
```
dependencies {
    implementation "com.github.pyamsoft.pydroid:billing:<version>"
}
```

## How to Use

Entry point is the `BillingModule` class, which exposes a `BillingInteractor`, `BillingConnector`,
and `BillingLauncher`
