# pydroid-bootstrap
Bootstrap an application quickly with common requirements
like version checking and library attribution.

## What is this

The data layers for bootstrapping a new application quickly

pydroid-bootstrap includes pre-built data layer logic for creating:

- An "About This Application" or "Open Source Libraries" screen
- Play Store app upgrade suggestions
- Play Store app review suggestions
- Linking to "Other Applications" made by the developer

This module only provides the data models and use case layer. It does not provide presentation
logic or UI on its own.

## Install

In your module's `build.gradle`:
```
dependencies {
    implementation "com.pyamsoft.pydroid:bootstrap:<version>"
}
```
