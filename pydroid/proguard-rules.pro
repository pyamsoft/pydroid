# Consumer proguard rules

# We are open source, we don't need obfuscation.
# We will still use optimizations though
-dontobfuscate

# Don't obfuscate causes the gradle build to fail after the optimization step
# The addition of !code/allocation/variable is needed to prevent this
-optimizations !code/allocation/variable

# Keep all of pydroid
-keep class com.pyamsoft.pydroid.* { *; }

# Silence some pydroid warnings
-dontwarn com.pyamsoft.pydroid.drawable.AsyncMapEntry$1
-dontwarn com.pyamsoft.pydroid.helper.AsyncMapHelper
-dontwarn com.pyamsoft.pydroid.ui.about.AboutLibrariesFragment$1$1

# RetroLambda
-dontwarn java.lang.invoke.LambdaForm$Hidden

# Silence some okio warnings
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn java.nio.file.Files
-dontwarn java.nio.file.Path
-dontwarn java.nio.file.OpenOption

# Retrofit2
# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote retrofit2.Platform
# Platform used when running on RoboVM on iOS. Will not be used at runtime.
-dontnote retrofit2.Platform$IOS$MainThreadExecutor
# Platform used when running on Java 8 VMs. Will not be used at runtime.
-dontwarn retrofit2.Platform$Java8
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions

# For use with proguard-android-optimize.txt
# Needed or else requests with crash with a IllegalArgumentException: Missng GET or @Url
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# Some other retrofit related warnings
-dontwarn retrofit2.adapter.rxjava.CompletableHelper$CompletableCallAdapter
-dontwarn retrofit2.adapter.rxjava.CompletableHelper$CompletableCallAdapter
-dontwarn retrofit2.adapter.rxjava.CompletableHelper$CompletableCallOnSubscribe

# Android Checkout uses weird annotations
-dontwarn javax.annotation.Nonnull
-dontwarn javax.annotation.Nullable
-dontwarn javax.annotation.concurrent.GuardedBy
-dontwarn javax.annotation.concurrent.Immutable
-dontwarn javax.annotation.concurrent.ThreadSafe
