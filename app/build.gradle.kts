plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.paranoid.privacylock"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.paranoid.privacylock"
        minSdk = 28
        targetSdk = 35
        versionCode = 5
        versionName = "v1.1.3"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    dependenciesInfo {
        includeInApk = false
        includeInBundle = false
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        debug {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
    kapt {
        correctErrorTypes = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.8.1")
    implementation("androidx.navigation:navigation-ui-ktx:2.8.1")
    implementation("androidx.activity:activity-ktx:1.9.2")

    //lottie
    implementation("com.airbnb.android:lottie:5.0.3")
    //shimmer facebook
    implementation("com.facebook.shimmer:shimmer:0.5.0")

    //shake detector
    implementation("com.squareup:seismic:1.0.3")

    //Dagger - Hilt
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-android-compiler:2.46")
    //implementation ("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.9.10") // Use the latest stable version

    //Splash Screen androidX
    implementation ("androidx.core:core-splashscreen:1.0.1")

    //Secure SharedPref
    implementation("androidx.security:security-crypto-ktx:1.1.0-alpha06")
}