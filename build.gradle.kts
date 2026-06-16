buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        //maps
        //classpath("com.google.gms:google-services:4.4.2")
        // firebase
        //classpath("com.google.firebase:firebase-crashlytics-gradle:3.0.4")
        //hilt
        classpath(libs.hilt.android.gradle.plugin)
    }
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false

    //dagger hilt
    id("com.google.dagger.hilt.android") version "2.56.1" apply false
    //firebase
    //id("com.google.gms.google-services") version "4.4.3" apply false
    //maps
    //id ("com.google.android.libraries.mapsplatform.secrets-gradle-plugin") version "2.0.1" apply false
}
