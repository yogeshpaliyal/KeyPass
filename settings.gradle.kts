pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }

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
