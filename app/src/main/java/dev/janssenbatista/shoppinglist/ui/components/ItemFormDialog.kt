package dev.janssenbatista.shoppinglist.ui.components

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import dev.janssenbatista.shoppinglist.R
import dev.janssenbatista.shoppinglist.data.entities.Item
import dev.janssenbatista.shoppinglist.ui.screens.shoppinglist.ItemState
import java.util.Date

@Composable
fun ItemFormDialog(
    shoppingListId: Long,
    onDismiss: () -> Unit,
    itemState: ItemState,
    isUpdating: Boolean = false
) {

    val itemFocusRequester = remember {
        FocusRequester()
    }
    val quantityFocusRequester = remember {
        FocusRequester()
    }

    LaunchedEffect(key1 = Unit) {
        itemFocusRequester.requestFocus()
    }

    var inputErrorMessage by remember {
        mutableStateOf("")
    }

    val context = LocalContext.current

    fun validateInputs(): Boolean {
        if (itemState.name.isBlank()) {
            inputErrorMessage = context.getString(R.string.item_name_cannot_be_blank)
            return false
        }
        try {
            itemState.onQuantityChange(itemState.quantity.replace(",", "."))
            itemState.quantity.toDouble()
        } catch (e: NumberFormatException) {
            inputErrorMessage = context.getString(R.string.invalid_quantity)
            return false
        }
        return true
    }

    Dialog(onDismissRequest = { onDismiss() }) {
        Card(
            colors = CardDefaults.cardColors()
                .copy(containerColor = MaterialTheme.colorScheme.background)
        ) {
            Column(
                Modifier
                    .padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.add_item),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )
                OutlinedTextField(
                    value = itemState.name,
                    enabled = !isUpdating,
                    singleLine = true,
                    onValueChange = {
                        itemState.onNameChange(it)
                    },
                    label = { Text(text = stringResource(R.string.item_name)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(itemFocusRequester),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { quantityFocusRequester.requestFocus() }
                    )
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = itemState.quantity,
                        onValueChange = {
                            itemState.onQuantityChange(it)
                        },
                        label = { Text(text = stringResource(R.string.quantity)) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier
                            .weight(1f)
                            .focusRequester(quantityFocusRequester)
                    )
                    OutlinedTextField(
                        value = itemState.unit,
                        onValueChange = {
                            itemState.onUnitChange(it)
                        },
                        label = { Text(text = stringResource(R.string.unity)) },
                        singleLine = true,
                        placeholder = { Text(text = stringResource(R.string.unity_example)) },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            capitalization = KeyboardCapitalization.Characters
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    Modifier.padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(onClick = {
                        if (validateInputs()) {
                            val item = Item(
                                shoppingListId = shoppingListId,
                                name = itemState.name,
                                quantity = itemState.quantity.toDouble(),
                                unit = itemState.unit,
                                isInTheCart = itemState.isInTheCart,
                                updatedAt = Date().time
                            )
                            itemState.apply {
                                onSaveItem(item)
                                clearFields()
                            }
                            onDismiss()
                        } else {
                            Toast.makeText(context, inputErrorMessage, Toast.LENGTH_SHORT).show()
                        }
                    }, Modifier.weight(1f)) {
                        Text(text = stringResource(id = R.string.save))
                    }
                    if (!isUpdating) {
                        Button(onClick = {
                            if (validateInputs()) {
                                val item = Item(
                                    shoppingListId = shoppingListId,
                                    name = itemState.name,
                                    quantity = itemState.quantity.toDouble(),
                                    unit = itemState.unit,
                                    isInTheCart = itemState.isInTheCart,
                                    updatedAt = Date().time
                                )
                                itemState.apply {
                                    onSaveItem(item)
                                    clearFields()
                                }
                                itemFocusRequester.requestFocus()
                            } else {
                                Toast.makeText(context, inputErrorMessage, Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }, Modifier.weight(2f)) {
                            Text(text = stringResource(R.string.add_another))
                        }
                    }
                }
            }
        }
    }
}