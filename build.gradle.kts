// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {

    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath ("com.android.tools.build:gradle:7.4.2")
        classpath ("org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}")
        classpath ("com.google.gms:google-services:4.3.15")

        classpath ("com.google.dagger:hilt-android-gradle-plugin:${Versions.hilt}")

        classpath ("com.spotify.ruler:ruler-gradle-plugin:1.4.0")
        classpath ("com.gradle:gradle-enterprise-gradle-plugin:3.13.2")
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle.kts files
    }
}
plugins {
    kotlin("multiplatform") version("1.8.0") apply false
    kotlin("android") version("1.8.0") apply false
    id("com.android.application") version("7.4.2") apply false
    id("com.android.library") version("7.4.2") apply false
    id("org.jetbrains.compose") version "1.4.0" apply false
    id ("com.diffplug.spotless") version("6.18.0")
    id("org.jetbrains.kotlin.kapt") version(Versions.kotlin)
    id("com.google.dagger.hilt.android") version ("2.46") apply false
    id("com.gradle.enterprise") version("3.13.2") apply false
}


subprojects {
    repositories {
        google()
        maven("https://jitpack.io")
    }

    apply(plugin = "com.diffplug.spotless")
    spotless {
        kotlin {
            target("**/*.kt")
            targetExclude("$buildDir/**/*.kt")
            targetExclude("bin/**/*.kt")

            val map = HashMap<String, String>();
            ktlint("0.46.0").userData(map)
           // licenseHeaderFile rootProject.file('spotless/copyright.kt')
        }
    }
}

//tasks.register("clean", Delete::class) {
//    delete(rootProject.buildDir)
//}
