import com.vanniktech.maven.publish.KotlinMultiplatform
import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTargetWithHostTests

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    // alias(libs.plugins.nokee.jni)
    alias(libs.plugins.nokee.cpp)
    alias(libs.plugins.publish)
}

description = "MPV Kotlin bindings"

@OptIn(ExperimentalKotlinGradlePluginApi::class)
kotlin {
    explicitApi()

    jvmToolchain(17)

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

    nativeTarget {
        compilations.getByName("main") {
            cinterops.create("libmpv")
        }
    }

    sourceSets {
        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
        named("jvmCommonMain") {
            dependencies {
                // implementation(projects.jvm)
                implementation(libs.jna.core)
            }
        }
        nativeMain {
            languageSettings {
                optIn("kotlinx.cinterop.ExperimentalForeignApi")
            }
        }
    }

    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }
}

android {
    namespace = group.toString()
    compileSdk = 34

    defaultConfig {
        minSdk = 21
    }
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL, automaticRelease = true)
    signAllPublications()
    coordinates(group.toString(), "mpv-kt", version.toString())
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

fun KotlinMultiplatformExtension.nativeTarget(
    configure: Action<KotlinNativeTarget>
): KotlinNativeTargetWithHostTests {
    val os = System.getProperty("os.name").lowercase()
    val nativeTarget = when {
        os.contains("win") -> mingwX64()
        os.contains("linux") -> linuxX64()
        os.contains("mac") -> macosX64()
        else -> throw IllegalStateException("Unsupported OS: $os")
    }

    configure(nativeTarget)

    return nativeTarget
}