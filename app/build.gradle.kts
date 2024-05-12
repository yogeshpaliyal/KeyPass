plugins {
    id("com.android.application")
    id("kotlin-android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
    id("com.spotify.ruler")
    id("com.google.dagger.hilt.android")
    id("org.jetbrains.kotlin.plugin.serialization")
}

val appPackageId = "com.yogeshpaliyal.keypass"

android {
    compileSdk = 34

    defaultConfig {

        applicationId = appPackageId
        minSdk = 23
        targetSdk = 34
        versionCode = 1427
        versionName = "1.4.27"

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
            isDefault=true
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
            "-Xopt-in=androidx.compose.material3.ExperimentalMaterial3Api"
        )
    }
    buildFeatures {
        compose = true
        viewBinding = true
    }

    flavorDimensions("default")



    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.4"
    }
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

    lint{
        disable += "MissingTranslation"
    }

}

ruler {
    abi.set("arm64-v8a")
    locale.set("en")
    screenDensity.set(480)
    sdkVersion.set(27)
}


dependencies {

    //api(project(":shared"))
    api(project(":common"))
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test:core-ktx:1.5.0")
    androidTestImplementation("androidx.test.ext:junit-ktx:1.1.5")
    // Test rules and transitive dependencies:
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:${Versions.compose}")
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
    implementation("androidx.navigation:navigation-compose:2.7.7")

    implementation("androidx.compose.material3:material3:1.2.1")
    implementation("com.google.accompanist:accompanist-themeadapter-material3:0.30.1")

    implementation("androidx.appcompat:appcompat:1.6.1")

    // XML Libraries
    implementation("org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

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
    androidTestImplementation("com.google.dagger:hilt-android-testing:2.51.1")
    // ...with Kotlin.
    kaptAndroidTest("com.google.dagger:hilt-android-compiler:${Versions.hilt}")

    // For Robolectric tests.
    testImplementation("com.google.dagger:hilt-android-testing:2.51.1")
    // ...with Kotlin.
    kaptTest("com.google.dagger:hilt-android-compiler:${Versions.hilt}")

    implementation("org.reduxkotlin:redux-kotlin-compose-jvm:0.6.0")
    implementation("me.saket.cascade:cascade-compose:2.2.0")

    implementation("androidx.biometric:biometric:1.1.0")

}

