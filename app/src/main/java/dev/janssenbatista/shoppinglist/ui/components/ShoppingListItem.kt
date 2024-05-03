package dev.janssenbatista.shoppinglist.ui.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.janssenbatista.shoppinglist.R
import dev.janssenbatista.shoppinglist.data.entities.Item

@Composable
fun ShoppingListItem(
    modifier: Modifier,
    item: Item,
    onItemChecked: () -> Unit,
    onDeleteItem: () -> Unit,
    onEditItem: () -> Unit
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked = item.isInTheCart, onCheckedChange = { onItemChecked() })
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            val itemQuantity = if (item.quantity - item.quantity.toInt() == 0.0) {
                item.quantity.toInt()
            } else {
                item.quantity
            }
            Text(
                text = item.name, style = if (item.isInTheCart) TextStyle.Default.copy(
                    textDecoration = TextDecoration.LineThrough
                ) else TextStyle.Default, fontSize = 20.sp
            )
            Text(
                text = "$itemQuantity ${item.unit}",
                color = if (isSystemInDarkTheme()) Color.White.copy(alpha = 0.5f) else Color.Black.copy(
                    alpha = 0.5f
                ),
                style = if (item.isInTheCart) TextStyle.Default.copy(
                    textDecoration = TextDecoration.LineThrough
                ) else TextStyle.Default,
                fontSize = 16.sp
            )
        }
        Row {
            IconButton(onClick = { onEditItem() }) {
                Icon(imageVector = Icons.Filled.Edit, contentDescription = stringResource(
                    R.string.edit_item,
                    item.name
                )
                )
            }
            IconButton(onClick = { onDeleteItem() }) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = stringResource(R.string.delete_item, item.name),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}