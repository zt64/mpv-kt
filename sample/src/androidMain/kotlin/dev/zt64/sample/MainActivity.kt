package dev.zt64.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dev.zt64.mpvkt.Mpv

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()

        super.onCreate(savedInstanceState)

        Mpv.clientApiVersion().also {
            println("Mpv client API version: $it")
        }
        setContent {
            Sample()
        }
    }
}