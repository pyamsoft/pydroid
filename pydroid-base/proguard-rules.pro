# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Silence some okio warnings
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn java.nio.file.Files
-dontwarn java.nio.file.Path
-dontwarn java.nio.file.OpenOption

# Retrofit2
-dontwarn javax.annotation.**
-dontwarn retrofit2.Platform$Java8
-dontnote retrofit2.Platform
-keepattributes Signature
-keepattributes Exceptions

# For use with proguard-android-optimize.txt
# Needed or else requests with crash with a IllegalArgumentException: Missng GET or @Url
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# autovalue GSON
-keepnames class **_GsonTypeAdapter
-keepnames @com.ryanharter.auto.value.gson.GenerateTypeAdapter class *