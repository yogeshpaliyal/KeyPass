plugins {
    id("com.android.library")
    id("kotlin-android")
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
    id("dagger.hilt.android.plugin")
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    compileSdk = 34

    defaultConfig {
        minSdk = 23

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    namespace = "com.yogeshpaliyal.common"
}

dependencies {

    api("androidx.core:core-ktx:1.16.0")
    api("androidx.appcompat:appcompat:1.7.1")

    // apache common codec
    implementation("commons-codec:commons-codec:1.18.0")

    // Androidx Security
    implementation("androidx.security:security-crypto:1.1.0-alpha07")

    api("androidx.documentfile:documentfile:1.1.0")

    api("androidx.room:room-runtime:${Versions.room}")
    kapt("androidx.room:room-compiler:${Versions.room}")
    api("androidx.room:room-ktx:${Versions.room}")

    // dependency injection
    api("com.google.dagger:hilt-android:${Versions.hilt}")
    kapt("com.google.dagger:hilt-android-compiler:${Versions.hilt}")
    api("androidx.hilt:hilt-work:1.2.0")
    // When using Kotlin.
    kapt("androidx.hilt:hilt-compiler:1.2.0")

    api("androidx.work:work-runtime-ktx:2.8.1")

    api("com.google.android.material:material:1.12.0")

    implementation("com.google.code.gson:gson:2.13.2")

    api("com.google.code.gson:gson:2.13.2")

    api("androidx.datastore:datastore-preferences:1.1.4")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")

    // Test
    implementation("androidx.test.ext:junit-ktx:1.2.1")
    androidTestApi("androidx.test:rules:1.6.1")

    implementation("net.zetetic:sqlcipher-android:4.7.2@aar")
    implementation("androidx.sqlite:sqlite:2.5.1")

    api("com.opencsv:opencsv:5.11")
}
