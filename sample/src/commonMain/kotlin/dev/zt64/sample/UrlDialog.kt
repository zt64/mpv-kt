package dev.zt64.sample

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun UrlDialog(
    url: String,
    onValueChange: (String) -> Unit,
    onDismissRequest: () -> Unit,
    onClickConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text("Enter a URL")
        },
        text = {
            TextField(
                value = url,
                onValueChange = {
                    onValueChange(it)
                },
                label = { Text("URL") },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    autoCorrect = false,
                    keyboardType = KeyboardType.Uri,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions { onClickConfirm() },
                singleLine = true
            )
        },
        confirmButton = {
            Button(onClick = onClickConfirm) {
                Text("OK")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismissRequest) {
                Text("Cancel")
            }
        }
    )
}