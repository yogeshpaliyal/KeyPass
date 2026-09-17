pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_PROJECT)
    repositories {
        google()
        mavenCentral()
    }
}

//gradleEnterprise {
//    // configuration
//    buildScan {
//        // Connecting to scans.gradle.com by agreeing to the terms of service
//        termsOfServiceUrl = "https://gradle.com/terms-of-service"
//        termsOfServiceAgree = "yes"
//    }
//}

rootProject.name = "KeyPass"
include(":app")
include(":shared")
include(":common")
//include(":desktop")
include(":baselineprofile")
