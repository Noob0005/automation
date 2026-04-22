# Add project specific ProGuard rules here.
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.myremote.admin.model.** { *; }
-dontwarn org.webrtc.**
-keep class org.webrtc.** { *; }
