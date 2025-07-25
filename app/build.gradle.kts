plugins {
    id("com.android.application")
    id("kotlin-android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
    id("com.spotify.ruler")
    id("com.google.dagger.hilt.android")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("org.jetbrains.kotlin.plugin.compose")
    id("androidx.baselineprofile")
}

val appPackageId = "com.yogeshpaliyal.keypass"

android {
    compileSdk = 35

    defaultConfig {

        applicationId = appPackageId
        minSdk = 23
        targetSdk = 35
        versionCode = 1440
        versionName = "1.4.40"

        testInstrumentationRunner = "com.yogeshpaliyal.keypass.CustomTestRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        missingDimensionStrategy("buildTypes", "release")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }

        debug {
            applicationIdSuffix = ".staging"
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    productFlavors {
        create("free") {
            isDefault = true
        }
        create("pro") {
            applicationIdSuffix = ".pro"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"

        freeCompilerArgs = listOf(
            "-Xopt-in=androidx.compose.material3.ExperimentalMaterial3Api",
        )
    }
    buildFeatures {
        compose = true
        viewBinding = true
    }

    flavorDimensions("default")

    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    namespace = appPackageId

    signingConfigs {
        getByName("debug") {
            storeFile = file("../keystores/debug.keystore")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }
    }

    lint {
        disable += "MissingTranslation"
        abortOnError = false
    }
}

ruler {
    abi.set("arm64-v8a")
    locale.set("en")
    screenDensity.set(480)
    sdkVersion.set(27)
}

dependencies {

    // api(project(":shared"))
    api(project(":common"))
    implementation("androidx.profileinstaller:profileinstaller:1.4.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation("androidx.test:core-ktx:1.6.1")
    androidTestImplementation("androidx.test.ext:junit-ktx:1.2.1")
    // Test rules and transitive dependencies:
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:${Versions.compose}")
    "baselineProfile"(project(":baselineprofile"))
    // Needed for createAndroidComposeRule, but not createComposeRule:
    debugImplementation("androidx.compose.ui:ui-test-manifest:${Versions.compose}")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:${Versions.compose}")
    debugImplementation("androidx.compose.ui:ui-tooling:${Versions.compose}")

    implementation(Deps.Compose.ui)
    implementation(Deps.Compose.uiTooling)
    implementation(Deps.Compose.uiToolingPreview)
    implementation(Deps.Compose.uiViewBinding)
    implementation(Deps.Compose.materialIconsExtended)
    implementation(Deps.Compose.activity)
    implementation(Deps.Compose.runtimeLiveData)
    implementation(Deps.Lifecycle.viewModelCompose)
    implementation(Deps.Lifecycle.viewModelKtx)
    implementation(Deps.Lifecycle.runtimeCompose)
    implementation("androidx.navigation:navigation-compose:2.9.1")

    implementation("androidx.compose.material3:material3:1.4.0-alpha14")
    implementation("androidx.compose.material3:material3-android:1.4.0-alpha14")

    implementation("com.google.accompanist:accompanist-themeadapter-material3:0.36.0")

    implementation("androidx.appcompat:appcompat:1.7.1")

    // XML Libraries
    implementation("org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")

    kapt("androidx.room:room-compiler:${Versions.room}")

    // dependency injection
    implementation("com.google.dagger:hilt-android:${Versions.hilt}")
    kapt("com.google.dagger:hilt-android-compiler:${Versions.hilt}")
    implementation("androidx.hilt:hilt-work:1.2.0")
    // When using Kotlin.
    kapt("androidx.hilt:hilt-compiler:1.2.0")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // zxing library
    // implementation "com.googl.ezxing:android-core:3.4.1"
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")

    // For instrumented tests.
    androidTestImplementation("com.google.dagger:hilt-android-testing:2.56.2")
    // ...with Kotlin.
    kaptAndroidTest("com.google.dagger:hilt-android-compiler:${Versions.hilt}")

    // For Robolectric tests.
    testImplementation("com.google.dagger:hilt-android-testing:2.56.2")
    // ...with Kotlin.
    kaptTest("com.google.dagger:hilt-android-compiler:${Versions.hilt}")

    implementation("org.reduxkotlin:redux-kotlin-compose-jvm:0.6.0")
    implementation("me.saket.cascade:cascade-compose:2.3.0")

    implementation("androidx.biometric:biometric:1.1.0")
}
