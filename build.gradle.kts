import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jlleitschuh.gradle.ktlint.KtlintExtension

plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.jvm) apply false

    alias(libs.plugins.android.library) apply false

    alias(libs.plugins.nokee.jni) apply false
    alias(libs.plugins.nokee.cpp) apply false
    alias(libs.plugins.javacpp) apply false

    alias(libs.plugins.ktlint) apply false
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

subprojects {
    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = "17"
            freeCompilerArgs += "-Xexpect-actual-classes"
        }
    }
}

apiValidation {
    ignoredProjects += listOf("jvm")
}