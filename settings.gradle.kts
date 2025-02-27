@file:Suppress("UnstableApiUsage")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
    }

    includeBuild("build-logic")
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
    }

    repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
}

rootProject.name = "mpv-kt"

include("mpv", "compose", "sample")