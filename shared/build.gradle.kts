plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.devtools.ksp")
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
                val room_version = "2.6.1"
                api("androidx.compose.runtime:runtime:1.6.8")
                api("androidx.compose.foundation:foundation:1.6.8")
                api("androidx.compose.material:material:1.6.8")
                implementation("org.reduxkotlin:redux-kotlin-compose:0.6.0")
                implementation("androidx.room:room-runtime:$room_version")
                add("kspJvm", "androidx.room:room-compiler:$room_version")


            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            dependencies {
                api("androidx.appcompat:appcompat:1.7.0")
                api("androidx.core:core-ktx:1.13.1")
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
