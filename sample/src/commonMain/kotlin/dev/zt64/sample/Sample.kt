package dev.zt64.sample

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mohamedrejeb.calf.picker.FilePickerFileType
import com.mohamedrejeb.calf.picker.FilePickerSelectionMode
import com.mohamedrejeb.calf.picker.rememberFilePickerLauncher
import dev.zt64.mpvkt.Mpv
import dev.zt64.mpvkt.compose.MpvPlayer
import dev.zt64.mpvkt.compose.rememberMpvState

sealed interface Destination {
    data object Home : Destination
    data class Player(val uri: String) : Destination
}

@Composable
fun Sample() {
    MaterialTheme(
        colorScheme = darkColorScheme()
    ) {
        var destination: Destination by remember { mutableStateOf(Destination.Home) }

        when (val dest = destination) {
            is Destination.Home -> Home(
                onSelectUri = { uri -> destination = Destination.Player(uri) }
            )

            is Destination.Player -> Player(
                uri = dest.uri,
                onClickBack = { destination = Destination.Home }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(
    onSelectUri: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Home") })
        }
    ) {
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
                    onResult = { files -> files.singleOrNull() }
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

                Button(onClick = pickerLauncher::launch) {
                    Text("Play from a file")
                }

                Button(onClick = { showUrlDialog = true }) {
                    Text("Play from a URL")
                }
            }

            Text(
                modifier = Modifier.align(Alignment.BottomCenter),
                text = "mpv client api version: ${Mpv.clientApiVersion()}"
            )
        }
    }
}

@Composable
fun Player(
    uri: String,
    onClickBack: () -> Unit
) {
    Surface {
        val interactionSource = remember { MutableInteractionSource() }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .hoverable(interactionSource)
        ) {
            val state = rememberMpvState(uri)
            var showControls by remember { mutableStateOf(false) }
            val isHovered by interactionSource.collectIsHoveredAsState()

            LaunchedEffect(showControls, isHovered) {
                showControls = isHovered
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
                        modifier = Modifier.align(Alignment.TopStart),
                        onClick = onClickBack
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }

                    IconButton(
                        modifier = Modifier.align(Alignment.Center),
                        onClick = { }
                    ) {
                        Crossfade(state.paused) { paused ->
                            if (paused) {
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
                    }

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
}