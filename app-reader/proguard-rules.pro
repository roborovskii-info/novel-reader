# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/ishimarusouhei/android-sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

##-------- Remove Logs
-assumenosideeffects class android.util.Log {
    public static int w(...);
    public static int i(...);
    public static int d(...);
    public static int v(...);
}

##-------- Android support library v4 & v7 --------
# Preserve Android support libraries classes and interfaces
-keep class android.support.** { *; }
-keep interface android.support.** { *; }

##-------- Fabric & Crashlytics --------
-keep public class * extends java.lang.Exception
-keepattributes SourceFile,LineNumberTable,*Annotation*
-printmapping mapping.txt

##-------- Simple-Xml --------
-dontwarn com.bea.xml.stream.**
-dontwarn org.simpleframework.xml.stream.**
-keep class org.simpleframework.xml.**{ *; }
-keepclassmembers,allowobfuscation class * {
    @org.simpleframework.xml.* <fields>;
    @org.simpleframework.xml.* <init>(...);
}

##-------- OkHttp --------
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }
-dontwarn com.squareup.okhttp.**

##-------- okio --------
-dontwarn java.nio.file.*
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

##-------- Nend SDK --------
-keep class net.nend.android.** { *; }
-dontwarn net.nend.android.**

##-------- Google Play Services --------
-keep class * extends java.util.ListResourceBundle {
    protected Object[][] getContents();
}

-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
    public static final *** NULL;
}

##-------- Google Analytics --------
-keep public class com.google.android.gms.analytics.** {
    public *;
}

##-------- AdMob --------
-keep public class com.google.android.gms.ads.** {
   public *;
}

-keep public class com.google.ads.** {
   public *;
}

-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
    @com.google.android.gms.common.annotation.KeepName *;
}

-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}