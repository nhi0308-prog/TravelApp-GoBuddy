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
        // ❌ KHÔNG cần jitpack ở đây
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()

        // ✅ ĐÚNG CHỖ
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "TravelApp-GoBuddy"
include(":app")