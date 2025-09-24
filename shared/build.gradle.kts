plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.kotlin.plugin.compose")
}

kotlin {
    androidTarget()
    jvmToolchain(17)
    jvm("desktop") {
        compilerOptions {
            apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_0)
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                api("androidx.compose.runtime:runtime:1.8.3")
                api("androidx.compose.foundation:foundation:1.8.3")
                api("androidx.compose.material:material:1.9.2")
                implementation("org.reduxkotlin:redux-kotlin-compose:0.6.0")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            dependencies {
                api("androidx.appcompat:appcompat:1.7.1")
                api("androidx.core:core-ktx:1.16.0")
            }
        }
        val androidUnitTest by getting {
            dependencies {
                implementation("junit:junit:4.13.2")
            }
        }
        val desktopMain by getting {
            dependencies {
            }
        }
        val desktopTest by getting
    }
}

android {
    compileSdk = 34
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 21
    }
    namespace = "com.yogeshpaliyal.keypass.common"
}
