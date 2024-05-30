package dev.zt64.sample

import androidx.compose.ui.window.singleWindowApplication

fun main() {
    singleWindowApplication(
        title = "mpv-kt compose sample"
    ) {
        Sample()
    }
}