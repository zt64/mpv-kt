package dev.zt64.mpvkt.gradle

import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import java.util.*

fun KotlinMultiplatformExtension.native(configure: KotlinNativeTarget.() -> Unit = {}) {
    val os = System.getProperty("os.name").lowercase()
    val targets = when {
        os.contains("win") -> listOf(mingwX64())

        os.contains("linux") -> listOf(
            linuxX64(),
            linuxArm64()
        )

        os.contains("mac") -> listOf(
            macosX64(),
            macosArm64(),
            iosX64(),
            iosArm64(),
            iosSimulatorArm64()
        )

        else -> throw IllegalStateException("Unsupported OS: $os")
    }

    targets.forEach(configure)
}

/**
 * Only configure apple targets if on macOS, otherwise don't
 *
 * @param configure
 */
fun KotlinMultiplatformExtension.apple(configure: KotlinNativeTarget.() -> Unit = {}) {
    val isMacOs = System.getProperty("os.name").lowercase(Locale.getDefault()).contains("mac")

    if (!isMacOs) return

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
        macosX64(),
        macosArm64()
    ).forEach(configure)
}