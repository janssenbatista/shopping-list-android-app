package dev.janssenbatista.shoppinglist.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import dev.janssenbatista.shoppinglist.R

@Composable
fun DeleteAlertDialog(
    title: String = "",
    text: String? = null,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        title = { Text(text = title) },
        text = { text?.let { Text(text = it) } },
        onDismissRequest = { onDismiss() }, confirmButton = {
            TextButton(onClick = { onConfirm() }) {
                Text(text = stringResource(R.string.yes))
            }
        }, dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text(text = stringResource(R.string.cancel))
            }
        })
}