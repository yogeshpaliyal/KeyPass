// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {

    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath ("com.android.tools.build:gradle:8.4.1")
        classpath ("org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}")
        classpath ("com.google.gms:google-services:4.4.1")

        classpath ("com.google.dagger:hilt-android-gradle-plugin:${Versions.hilt}")

        classpath ("com.spotify.ruler:ruler-gradle-plugin:2.0.0-beta-1")
        classpath ("com.gradle:gradle-enterprise-gradle-plugin:3.13.2")
        classpath("org.jetbrains.kotlin:compose-compiler-gradle-plugin:${Versions.kotlin}")
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle.kts files
    }
}
plugins {
    kotlin("multiplatform") version("1.8.0") apply false
    kotlin("android") version("1.8.0") apply false
    id("com.android.application") version("7.4.2") apply false
    id("com.android.library") version("7.4.2") apply false
    id ("com.diffplug.spotless") version("6.18.0")
    id("org.jetbrains.kotlin.kapt") version(Versions.kotlin)
    id("com.google.dagger.hilt.android") version ("2.51.1") apply false
    id("com.gradle.enterprise") version("3.17.4") apply false
    id("org.jetbrains.kotlin.plugin.serialization") version (Versions.kotlin)
}


subprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }

    apply(plugin = "com.diffplug.spotless")
    spotless {
        kotlin {
            target("**/*.kt")
            targetExclude("$buildDir/**/*.kt")
            targetExclude("bin/**/*.kt")

            val map = HashMap<String, String>()
            ktlint("0.46.0").userData(map)
           // licenseHeaderFile rootProject.file('spotless/copyright.kt')
        }
    }
}

//tasks.register("clean", Delete::class) {
//    delete(rootProject.buildDir)
//}

// pre build gradle hook for git init on evey gradle build to reduce developer friction.
val installGitHook by tasks.register<Exec>("installGitHook") {
    workingDir = rootProject.rootDir
    commandLine = listOf("sh", "./githooks/git-init.sh")
}

tasks.getByPath("app:assemble").dependsOn(installGitHook)
