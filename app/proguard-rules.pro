# =============================================================================
# 1. 기본 설정 (Attributes & Kotlin)
# =============================================================================
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keepattributes EnclosingMethod
-keepattributes InnerClasses
-dontoptimize

# Kotlin Metadata 유지 (리플렉션 및 Coroutine 동작에 필수)
-keepclassmembers class kotlin.Metadata { *; }

# =============================================================================
# 2. Kotlin Serialization (핵심)
# =============================================================================
-keepclassmembers class * {
    @kotlinx.serialization.Serializable <class>;
}

# Serializer 로직을 찾기 위한 Companion Object 및 생성자 유지
-keepclassmembers class * {
    *** Companion;
}
-keepclassmembers class * {
    void <init>();
    *** serializer(...);
}

# Enum 클래스 직렬화 지원
-keepclassmembers enum * {
    *** Companion;
}

# =============================================================================
# 3. Retrofit 2
# =============================================================================
-keep interface * {
    @retrofit2.http.* <methods>;
}

# 패키지 경로가 바뀌지 않도록 API 인터페이스 보호
-keep interface com.joke.mon.**.api.** { *; }

# =============================================================================
# 4. App Specific (DTO & Data Layer)
# =============================================================================
-keep class com.joke.mon.**.dto.** { *; }

-keep class com.joke.mon.**.data.** { *; }

# =============================================================================
# 5. Hilt / Dagger (Dependency Injection)
# =============================================================================
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.android.internal.lifecycle.HiltViewModelFactory { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }
-keep class com.joke.mon.Dagger** { *; }
-keep class com.joke.mon.Hilt_** { *; }
-keep class **_Factory { *; }

# =============================================================================
# 6. Third Party Libraries & Warnings
# =============================================================================
-dontwarn com.google.android.gms.common.annotation.NoNullnessRewrite
-dontwarn com.google.android.play.core.ktx.**
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn retrofit2.**
-dontwarn androidx.compose.**
-dontwarn androidx.activity.**
-dontwarn androidx.lifecycle.**
-dontwarn androidx.room.**
-dontwarn kotlinx.serialization.**
-dontwarn com.google.firebase.**

# Firebase
-keep class com.google.firebase.** { *; }

# Room
-keep class androidx.room.** { *; }