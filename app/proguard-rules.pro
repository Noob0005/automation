# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /sdk/tools/proguard/proguard-android.txt

# Keep Firebase
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }

# Keep WebRTC
-keep class org.webrtc.** { *; }

# Keep Gson
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.myremote.admin.data.** { *; }
-keepclassmembers class com.myremote.admin.data.** { *; }

# Kotlin
-dontwarn kotlin.**
-keepclassmembers class **$WhenMappings {
    <fields>;
}
-keepclassmembers class kotlinx.coroutines.** {
    <fields>;
    <methods>;
}
