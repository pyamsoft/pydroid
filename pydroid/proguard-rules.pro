# Consumer proguard rules

# We are open source, we don't need obfuscation.
# We will still use optimizations though
-dontobfuscate

# Don't obfuscate causes the gradle build to fail after the optimization step
# The addition of !code/allocation/variable is needed to prevent this
-optimizations !code/allocation/variable

# Silence some pydroid warnings
-dontwarn com.pyamsoft.pydroid.ui.about.AboutLibrariesFragment

# RetroLambda
-dontwarn java.lang.invoke.LambdaForm

# Silence some okio warnings
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn java.nio.file.Files
-dontwarn java.nio.file.Path
-dontwarn java.nio.file.OpenOption

# Retrofit2
-dontwarn retrofit2.Platform$Java8
-dontwarn retrofit2.adapter.rxjava.CompletableHelper$CompletableCallAdapter
-dontwarn retrofit2.adapter.rxjava.CompletableHelper$CompletableCallOnSubscribe
-dontwarn retrofit2.adapter.rxjava.CompletableHelper$CompletableCallOnSubscribe$1
-dontnote retrofit2.Platform
-keepattributes Signature
-keepattributes Exceptions

# For use with proguard-android-optimize.txt
# Needed or else requests with crash with a IllegalArgumentException: Missng GET or @Url
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
