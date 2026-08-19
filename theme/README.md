# pydroid-theme
PYDroid MaterialTheme extensions

## What is this

Useful extensions to MaterialTheme

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
    implementation "com.github.pyamsoft.pydroid:theme:<version>"
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
    implementation(libs.pydroid.theme)
}
```

in `gradle/libs.versions.toml`
```toml
[versions]
pydroid = "<version>"

[libraries]
pydroid-theme = { group = "com.github.pyamsoft.pydroid", name = "theme", version.ref = "pydroid" }
```

## How to Use

See `MaterialTheme.keylines` and `LocalKeylines` for theme based spacing. See `KeylineDefaults` for default values.
