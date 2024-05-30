
import com.vanniktech.maven.publish.KotlinMultiplatform
import com.vanniktech.maven.publish.SonatypeHost
import dev.zt64.mpvkt.gradle.native
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    id("kmp-lib")
    alias(libs.plugins.android.library)
    alias(libs.plugins.kover)
    alias(libs.plugins.publish)
}

description = "MPV Kotlin bindings"

@OptIn(ExperimentalKotlinGradlePluginApi::class)
kotlin {
    explicitApi()

    applyDefaultHierarchyTemplate {
        common {
            group("jvmCommon") {
                withAndroidTarget()
                withJvm()
            }
        }
    }

    jvm()

    androidTarget {
        publishLibraryVariants("release")
    }

    native {
        compilations.getByName("main") {
            cinterops.create("libmpv")
        }
    }

    sourceSets {
        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.io)
            }
        }

        named("jvmCommonMain") {
            dependencies {
                implementation(projects.jni)
            }
        }
    }

    compilerOptions {
        optIn.addAll("kotlinx.cinterop.ExperimentalForeignApi")
    }
}

android {
    namespace = "$group"
    compileSdk = 34

    defaultConfig {
        minSdk = 21
    }
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL, automaticRelease = true)
    signAllPublications()
    coordinates("$group", "mpv-kt", "$version")
    configure(KotlinMultiplatform(sourcesJar = true))

    pom {
        name = "mpv-kt"
        description = "MPV Kotlin bindings"
        inceptionYear = "2024"
        url = "https://github.com/zt64/mpv-kt"

        licenses {
            license {
                name = "MIT"
                url = "https://opensource.org/licenses/MIT"
            }
        }

        developers {
            developer {
                id = "zt64"
                name = "zeet"
                url = "https://zt64.dev"
            }
        }

        scm {
            url = "https://github.com/zt64/mpv-kt"
            connection = "scm:git:github.com/zt64/mpv-kt.git"
            developerConnection = "scm:git:ssh://github.com/zt64/mpv-kt.git"
        }
    }
}