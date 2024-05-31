pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }

}
dependencyResolutionManagement {
    defaultLibrariesExtensionName = "projectLibs"
    repositoriesMode.set(RepositoriesMode.PREFER_PROJECT)
    repositories {
        google()
        mavenCentral()
    }
    versionCatalogs {
        create("libs") { from(files("gradle/libs.versions.toml")) }
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