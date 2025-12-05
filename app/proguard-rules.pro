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
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keepattributes EnclosingMethod
-keepattributes InnerClasses
-dontoptimize
-keepclassmembers class kotlin.Metadata { *; }

# data 패키지와 api 패키지는 통째로 유지 (DTO, Retrofit Interface 보호)
-keep class com.joke.mon.**.data.** { *; }
-keep class com.joke.mon.**.api.** { *; }

-dontwarn com.google.android.gms.common.annotation.NoNullnessRewrite
-dontwarn com.google.android.play.core.ktx.**
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn retrofit2.**
-dontwarn androidx.compose.**
-dontwarn androidx.activity.**
-dontwarn androidx.lifecycle.**
-dontwarn androidx.room.**


# Retrofit
-keep interface * {
    @retrofit2.http.* <methods>;
}

-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
    <init>(...);
}

-keep,allowobfuscation @interface com.google.gson.annotations.SerializedName

-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.android.internal.lifecycle.HiltViewModelFactory { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }
-keep class com.joke.mon.Dagger** { *; }
-keep class com.joke.mon.Hilt_** { *; }
-keep class **_Factory { *; }


-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**

-keep class androidx.room.** { *; }
