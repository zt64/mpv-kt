import dev.zt64.mpvkt.gradle.apple

plugins {
    id("kmp-lib")
    alias(libs.plugins.kotlin.multiplatform)
    // alias(libs.plugins.kotlin.cocoapods)
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose.jb)
}

kotlin {
    jvmToolchain(17)

    jvm()
    androidTarget()
    apple()

    // cocoapods {
    //     version = "1.0.0"
    //     summary = "Compose application framework"
    //     homepage = "empty"
    //     ios.deploymentTarget = "11.0"
    //     podfile = project.file("../iosApp/Podfile")
    //     framework {
    //         baseName = "ComposeApp"
    //         isStatic = true
    //     }
    // }

    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.mpv)

                implementation(projects.compose)
                implementation(compose.material3)
                implementation(compose.materialIconsExtended)
                implementation(libs.calf.filepicker)
            }
        }

        jvmMain {
            dependencies {
                implementation(compose.desktop.currentOs)
            }
        }

        androidMain {
            dependencies {
                implementation(libs.androidx.activity)
                implementation(libs.androidx.appcompat)
            }
        }
    }
}

android {
    namespace = "dev.zt64.sample"
    compileSdk = 34

    ndkVersion = "28.0.12433566"

    defaultConfig {
        targetSdk = 34
        minSdk = 21

        externalNativeBuild {
            cmake {
                arguments(
                    "-DANDROID_ARM_NEON=1",
                    "-DANDROID_STL=c++_shared"
                )
            }
        }

        ndk {
            abiFilters += listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
        }
    }

    externalNativeBuild {
        cmake {
            path = file("src/androidMain/cpp/CMakeLists.txt")
        }
    }
}

compose {
    desktop.application {
        mainClass = "dev.zt64.sample.MainKt"
    }
}