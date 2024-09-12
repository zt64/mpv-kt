import org.jlleitschuh.gradle.ktlint.KtlintExtension

plugins {
    // Kotlin
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.cocoapods) apply false

    // Compose
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.compose.jb) apply false

    // Android
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.android.application) apply false

    // Maintenance
    alias(libs.plugins.dockcross) apply false
    alias(libs.plugins.shadow) apply false
    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.kover) apply false
    alias(libs.plugins.compatibility)
}

allprojects {
    apply {
        plugin("org.jlleitschuh.gradle.ktlint")
    }

    configure<KtlintExtension> {
        version = rootProject.libs.versions.ktlint
    }

    group = "dev.zt64.mpvkt"
    version = "1.0.0"
}

apiValidation {
    ignoredProjects += listOf("sample")
}