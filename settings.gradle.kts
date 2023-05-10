pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}


plugins {
    id("com.gradle.enterprise") version("3.13.2")
}


gradleEnterprise {
    // configuration
    buildScan {
        // Connecting to scans.gradle.com by agreeing to the terms of service
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"
    }
}

rootProject.name = "KeyPass"
include(":app")
include(":common")