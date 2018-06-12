# Consumer proguard rules

# We are open source, we don't need obfuscation.
# We will still use optimizations though
-dontobfuscate

# Don't obfuscate causes the gradle build to fail after the optimization step
# The addition of !code/allocation/variable is needed to prevent this
-optimizations !code/allocation/variable

# Silence some pydroid warnings
-dontwarn com.pyamsoft.pydroid.**
