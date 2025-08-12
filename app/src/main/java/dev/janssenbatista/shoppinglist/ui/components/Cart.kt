package dev.janssenbatista.shoppinglist.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.janssenbatista.shoppinglist.R
import dev.janssenbatista.shoppinglist.data.entities.Item
import dev.janssenbatista.shoppinglist.ui.screens.shoppinglist.ItemState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun Cart(
    itemsAtCart: List<Item>,
    itemState: ItemState,
    onEditItem: (Item) -> Unit,
    isCartOpen: Boolean,
    setCartOpen: () -> Unit
) {

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    if (isCartOpen && itemsAtCart.isNotEmpty()) {
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = { setCartOpen() }
        ) {
            Column {
                Text(
                    text = stringResource(
                        R.string.car_items,
                        itemsAtCart.size,
                        if (itemsAtCart.size == 1) stringResource(R.string.item)
                        else stringResource(R.string.items)
                    ),
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxWidth(),
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                LazyColumn(Modifier
                    .padding(bottom = 16.dp)
                    .animateContentSize()) {
                    items(itemsAtCart, key = { item -> item.name + item.shoppingListId }) { item ->
                        Item(
                            Modifier
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .clickable {
                                    itemState.onSaveItem(
                                        item.copy(
                                            isInTheCart = !item.isInTheCart
                                        )
                                    )
                                }
                                .animateItemPlacement()
                                .fillMaxWidth(),
                            item = item,
                            onDeleteItem = {
                                itemState.onDeleteItem(item)
                            },
                            onEditItem = {
                                onEditItem(item)
                            },
                            onItemChecked = {
                                itemState.onSaveItem(
                                    item.copy(
                                        isInTheCart = !item.isInTheCart
                                    )
                                )
                            })
                    }
                }
            }
        }
    }
}