import dev.zt64.mpvkt.gradle.apple

plugins {
    id("kmp-lib")
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose.jb)
}

description = "MPV Kotlin bindings for Compose"

kotlin {
    explicitApi()

    jvm()
    apple()

    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.mpv)
                implementation(compose.foundation)
            }
        }
    }
}