pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
//        maven(url = "https://jitpack.io")
    }
}
include(":app")
include(":ioka")

//if (!System.env.JITPACK) {
//    rootProject.name = "ioka Demo App"
//
//    include ':app'
//}
