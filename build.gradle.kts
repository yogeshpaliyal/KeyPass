// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {

    repositories {
        google()
    }
    dependencies {
        classpath ("com.android.tools.build:gradle:7.4.2")
        classpath ("org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}")
        classpath ("com.google.gms:google-services:4.3.15")
        classpath ("androidx.navigation:navigation-safe-args-gradle-plugin:${Versions.navigation}")


        classpath ("com.google.dagger:hilt-android-gradle-plugin:${Versions.hilt}")

        classpath ("com.spotify.ruler:ruler-gradle-plugin:1.4.0")
        classpath ("com.gradle:gradle-enterprise-gradle-plugin:3.13.2")
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle.kts files
    }
}
plugins {
    id ("com.diffplug.spotless") version("6.18.0")
}

/*task clean(type: Delete) {
    delete rootProject.buildDir
}*/



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