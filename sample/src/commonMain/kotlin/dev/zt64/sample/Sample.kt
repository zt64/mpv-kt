package dev.zt64.sample

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mohamedrejeb.calf.picker.FilePickerFileType
import com.mohamedrejeb.calf.picker.FilePickerSelectionMode
import com.mohamedrejeb.calf.picker.rememberFilePickerLauncher
import dev.zt64.mpvkt.Mpv
import dev.zt64.mpvkt.compose.MpvPlayer
import dev.zt64.mpvkt.compose.rememberMpvState
import kotlinx.coroutines.delay

sealed class Destination {
    data object Home : Destination()

    data class Player(val uri: String) : Destination()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Sample() {
    MaterialTheme(
        colorScheme = darkColorScheme()
    ) {
        var destination: Destination by rememberSaveable { mutableStateOf(Destination.Home) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = when (destination) {
                                is Destination.Home -> "Home"
                                is Destination.Player -> "Player"
                            }
                        )
                    },
                    navigationIcon = {
                        if (destination !is Destination.Home) {
                            IconButton(onClick = { destination = Destination.Home }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back"
                                )
                            }
                        }
                    }
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (val dest = destination) {
                    is Destination.Home -> Home(
                        onSelectUri = { uri ->
                            destination = Destination.Player(uri)
                        }
                    )

                    is Destination.Player -> Player(dest.uri)
                }
            }
        }
    }
}

@Composable
fun Home(onSelectUri: (String) -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val pickerLauncher = rememberFilePickerLauncher(
                type = FilePickerFileType.Video,
                selectionMode = FilePickerSelectionMode.Single,
                onResult = { files ->
                    files.singleOrNull()
                }
            )
            var showUrlDialog by remember { mutableStateOf(false) }

            if (showUrlDialog) {
                var url by remember { mutableStateOf("") }

                UrlDialog(
                    url = url,
                    onValueChange = { url = it },
                    onDismissRequest = { showUrlDialog = false },
                    onClickConfirm = {
                        showUrlDialog = false
                        onSelectUri(url)
                    }
                )
            }

            // Button(onClick = pickerLauncher::launch) {
            //     Text("Select a file")
            // }

            Button(onClick = { showUrlDialog = true }) {
                Text("Enter a URL")
            }
        }

        Text(
            modifier = Modifier.align(Alignment.BottomCenter),
            text = "mpv client api version: ${Mpv.clientApiVersion()}"
        )
    }
}

@Composable
fun Player(uri: String) {
    val state = rememberMpvState(uri)
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .hoverable(interactionSource)
    ) {
        var showControls by remember { mutableStateOf(false) }
        val isHovered by interactionSource.collectIsHoveredAsState()

        LaunchedEffect(showControls, isHovered) {
            if (isHovered) {
                showControls = true
            } else {
                delay(1000)
                showControls = false
            }
        }

        MpvPlayer(
            modifier = Modifier.matchParentSize(),
            state = state
        )

        AnimatedVisibility(
            modifier = Modifier.fillMaxSize(),
            visible = showControls,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f))
                    .padding(16.dp)
            ) {
                IconButton(
                    modifier = Modifier.align(Alignment.Center),
                    onClick = { }
                ) {
                    if (state.paused) {
                        Icon(
                            imageVector = Icons.Default.PlayCircle,
                            contentDescription = "Play"
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.PauseCircle,
                            contentDescription = "Pause"
                        )
                    }
                }

                rememberScrollState()

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                ) {
                    Row {
                        Text(state.position.toString())
                        Spacer(Modifier.weight(1f))
                        Text(state.duration.toString())
                    }

                    Spacer(Modifier.height(8.dp))

                    Slider(
                        value = state.position,
                        onValueChange = { }
                    )
                }
            }
        }
    }
}