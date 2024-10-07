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
##---------------Begin: proguard configuration common for all Android apps ----------
-optimizationpasses 10
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-dontpreverify
-overloadaggressively
-useuniqueclassmembernames

-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

## Переименование пакетов
#-repackageclasses ''
#-flattenpackagehierarchy
#-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

-allowaccessmodification
#-keepattributes *Annotation*
-renamesourcefileattribute SourceFile
#-keepattributes SourceFile,LineNumberTable

#-keep public class * extends android.app.Activity
#-keep public class * extends android.app.Application
#-keep public class * extends android.app.Service
#-keep public class * extends android.content.BroadcastReceiver
#-keep public class * extends android.content.ContentProvider
#-keep public class * extends android.app.backup.BackupAgentHelper
#-keep public class * extends android.preference.Preference
#-keep public class com.android.vending.licensing.ILicensingService
#-dontnote com.android.vending.licensing.ILicensingService

# Explicitly preserve all serialization members. The Serializable interface
# is only a marker interface, so it wouldn't save them.

#
## Preserve all native method names and the names of their classes.
#-keepclasseswithmembernames class * {
#    native <methods>;
#}
#
#-keepclasseswithmembernames class * {
#    public <init>(android.content.Context, android.util.AttributeSet);
#}
#
#-keepclasseswithmembernames class * {
#    public <init>(android.content.Context, android.util.AttributeSet, int);
#}

# Preserve static fields of inner classes of R classes that might be accessed
# through introspection.
##---------------End: proguard configuration common for all Android apps ----------
-dontwarn com.google.api.client.http.GenericUrl
-dontwarn com.google.api.client.http.HttpHeaders
-dontwarn com.google.api.client.http.HttpRequest
-dontwarn com.google.api.client.http.HttpRequestFactory
-dontwarn com.google.api.client.http.HttpResponse
-dontwarn com.google.api.client.http.HttpTransport
-dontwarn com.google.api.client.http.javanet.NetHttpTransport$Builder
-dontwarn com.google.api.client.http.javanet.NetHttpTransport
-dontwarn javax.lang.model.element.Modifier
-dontwarn org.joda.time.Instant
