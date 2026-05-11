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

##########
# Mantener modelos
##########
-keep class avill.ladv.literaturestudyguide.model.** { *; }
-keep class avill.ladv.literaturestudyguide.data.local.db.room.entities.** { *; }
##########
# Room
##########
-keep class androidx.room.** { *; }
-keep @androidx.room.Dao public interface * { *; }
-keepclassmembers class * {
    @androidx.room.* <methods>;
}
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn androidx.room.paging.**
##########
# ViewModels (MVVM)
##########
-keep class * extends androidx.lifecycle.ViewModel { *; }

##########
# Hilt / Dagger
##########
-keep class dagger.hilt.** { *; }
-keep class * {
    @dagger.hilt.InstallIn <methods>;
}
-keep class * {
    @dagger.Module <methods>;
}
-keep class * {
    @dagger.Provides <methods>;
}
-keep class * {
    @javax.inject.Inject <init>(...);
}

##########
# Evitar advertencias de Hilt/Dagger
##########
-dontwarn dagger.**
-dontwarn javax.inject.**
-dontwarn androidx.hilt.**