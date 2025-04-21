// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {

    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath ("com.android.tools.build:gradle:8.9.2")
        classpath ("org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}")
        classpath ("com.google.gms:google-services:4.4.2")

        classpath ("com.google.dagger:hilt-android-gradle-plugin:${Versions.hilt}")

        classpath ("com.spotify.ruler:ruler-gradle-plugin:2.0.0-beta-3")
        classpath ("com.gradle:gradle-enterprise-gradle-plugin:3.13.2")
        classpath("org.jetbrains.kotlin:compose-compiler-gradle-plugin:${Versions.kotlin}")
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle.kts files
    }
}
plugins {
    kotlin("multiplatform") version("2.0.21") apply false
    kotlin("android") version("2.0.21") apply false
    id("com.android.application") version("7.4.2") apply false
    id("com.android.library") version("7.4.2") apply false
    id("org.jetbrains.kotlin.kapt") version(Versions.kotlin)
    id("com.google.dagger.hilt.android") version ("2.56.2") apply false
    id("com.gradle.enterprise") version("3.19.2") apply false
    id("org.jetbrains.kotlin.plugin.serialization") version (Versions.kotlin)
    id("androidx.baselineprofile") version "1.3.4" apply false
    id("com.android.test") version "8.9.1" apply false
}


subprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }

}

//tasks.register("clean", Delete::class) {
//    delete(rootProject.buildDir)
//}

// pre build gradle hook for git init on evey gradle build to reduce developer friction.
//val installGitHook by tasks.register<Exec>("installGitHook") {
//    workingDir = rootProject.rootDir
//    commandLine = listOf("sh", "./githooks/git-init.sh")
//}

//tasks.getByPath("app:assemble").dependsOn(installGitHook)
