plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    //kpt
    id("kotlin-kapt")
    //kotlin
    id("kotlin-parcelize")
    //hilt
    id("com.google.dagger.hilt.android")
    //services
    id("com.google.gms.google-services")
    //firebase
    id("com.google.firebase.crashlytics")
}
/*get The sha1
./gradlew signingReport*/
android {
    namespace = "avill.ladv.chordo"
    compileSdk = 36

    defaultConfig {
        applicationId = "avill.ladv.chordo"
        minSdk = 25
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // Enable Crashlytics for release builds
            isDebuggable = false
            /*firebaseCrashlytics {
                mappingFileUploadEnabled = true
            }*/
        }

    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    tasks.withType<JavaCompile> {
        options.compilerArgs.add("-Xlint:deprecation")
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))

    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation("androidx.compose.material:material:1.8.3")

    implementation(libs.androidx.material3)
    implementation(libs.play.services.location)
    //implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    //local libraries
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))
    //todo viewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.9.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.4.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.9.1")
    //todo coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.9")
    implementation("androidx.compose.runtime:runtime-livedata:1.8.3")
    //todo hilt
    implementation("com.google.dagger:hilt-android:2.56.1")
    kapt("com.google.dagger:hilt-compiler:2.56.2")
    implementation ("androidx.hilt:hilt-work:1.2.0")
    kapt ("androidx.hilt:hilt-compiler:1.2.0")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    //kapt("com.google.dagger:hilt-android-compiler:2.50")
    //todo Room
    implementation("androidx.room:room-runtime:2.7.2")
    kapt("androidx.room:room-compiler:2.7.1")
    implementation ("androidx.room:room-ktx:2.7.2")
    //DataStore Preferences
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    //todo Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.retrofit.converter.scalars)
    //implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.3")
    //todo okhttp3
    implementation(platform("com.squareup.okhttp3:okhttp-bom:5.1.0"))
    implementation("com.squareup.okhttp3:okhttp")
    implementation("com.squareup.okhttp3:logging-interceptor")
    //jsonConverter
    implementation(libs.coil.compose)
    //----------------------------------------------------------------------------------------------
    //todo: firebase
    implementation(libs.firebase.firestore.ktx)
    implementation("com.google.firebase:firebase-database:21.0.0")
    //Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:33.16.0"))

    implementation("com.google.firebase:firebase-firestore")
    // Add the dependencies for Firebase products you want to use
    // When using the BoM, don't specify versions in Firebase dependencies
    //implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-messaging")
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-crashlytics")

    implementation("com.google.firebase:firebase-core:21.1.1")
    //firebase auth
    implementation("com.google.firebase:firebase-auth")
    //----------------------------------------------------------------------------------------------
    //todo maps
    implementation(libs.maps.compose)
    implementation("com.google.android.gms:play-services-maps:19.2.0")
    //maps clustering
    implementation("com.google.maps.android:android-maps-utils:2.3.0")
    //maps places
    implementation("com.google.android.libraries.places:places:2.6.0")
    //location
    implementation("com.google.android.gms:play-services-location:21.0.1")
    //maps Directions API
    implementation("com.google.maps:google-maps-services:2.2.0")
    implementation("org.slf4j:slf4j-simple:1.7.25")
    //todo ADDS-------------------------------------------------------------------------------------
    implementation ("com.google.android.gms:play-services-ads:22.2.0")
    //----------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------
    //glide load images
    implementation("com.github.bumptech.glide:glide:4.16.0")
    //gif
    //implementation("pl.droidsonroids.gif:android-gif-drawable:1.2.27")
    //svg images
    implementation("io.coil-kt:coil-svg:2.4.0")
    //qr
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.5")
    implementation("androidx.multidex:multidex:2.0.1")

    // Easy Permissions
    implementation ("com.vmadalin:easypermissions-ktx:1.0.0")
    //wifi
    implementation("io.github.thanosfisherman.wifiutils:wifiutils:1.6.6")
    //work runtime for auto boot
    implementation("androidx.work:work-runtime-ktx:2.9.0")
    //video exo player animation for the login
    implementation("com.google.android.exoplayer:exoplayer:2.19.1")
    //media3
    implementation("androidx.media3:media3-ui:1.9.0")
    implementation("androidx.media3:media3-exoplayer:1.9.0")
    implementation("androidx.media3:media3-session:1.2.1")
    //trigger
    //implementation ("com.jakewharton:process-phoenix:2.1.2")
    //View------------------------------------------------------------------------------------------
    //splash screen
    implementation("androidx.core:core-splashscreen:1.2.0")
    // collapsing Toolbar
    implementation("me.onebone:toolbar-compose:2.3.5")
    //navigation
    implementation("androidx.navigation:navigation-compose:2.7.4")
    //----------------------------------------------------------------------------------------------
    // Optional -- Mockito framework
    testImplementation("org.mockito:mockito-core:5.21.0")
    //camera
    implementation("androidx.camera:camera-camera2:1.5.2")
    implementation("androidx.camera:camera-view:1.5.2")
    implementation("androidx.compose.material:material-icons-extended")
}
kapt {
    correctErrorTypes = true
}