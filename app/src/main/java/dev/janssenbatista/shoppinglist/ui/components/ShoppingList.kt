package dev.janssenbatista.shoppinglist.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.janssenbatista.shoppinglist.R
import dev.janssenbatista.shoppinglist.data.entities.Item
import dev.janssenbatista.shoppinglist.ui.screens.shoppinglist.ShoppingListState
import dev.janssenbatista.shoppinglist.ui.utils.WindowSize
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ShoppingList(
    shoppingListState: ShoppingListState,
    selectedShoppingListId: Long?,
    drawerState: DrawerState,
    windowSize: WindowSize,
    onEditItem: (Item) -> Unit,
    onDeleteItem: (Item) -> Unit,
    onItemChecked: (Item) -> Unit,
) {

    val scope = rememberCoroutineScope()

    selectedShoppingListId?.let {
        AnimatedVisibility(
            visible = shoppingListState.items.isNotEmpty() && !shoppingListState.isLoadingItems,
            enter = slideInVertically() + fadeIn(),
        ) {
            LazyColumn(
                modifier = Modifier
                    .animateContentSize(),
                contentPadding = PaddingValues(bottom = 64.dp)
            ) {
                itemsIndexed(
                    items = shoppingListState.items,
                    key = { _, item ->
                        item.name
                    }) { index, item ->
                    Item(
                        Modifier
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .animateItemPlacement()
                            .fillMaxWidth(),
                        item = item,
                        onDeleteItem = {
                            onDeleteItem(item)
                        },
                        onEditItem = {
                            onEditItem(item)
                        },
                        onItemChecked = {
                            onItemChecked(item)
                        })
                    if (index < shoppingListState.items.size - 1) {
                        HorizontalDivider()
                    }
                }
            }
        }
        if (shoppingListState.items.isEmpty() && !shoppingListState.isLoadingItems) {
            Box(
                Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    Modifier.padding(bottom = 128.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Image(
                        painter = if (isSystemInDarkTheme())
                            painterResource(id = R.drawable.list_white)
                        else painterResource(
                            R.drawable.list_black
                        ),
                        contentDescription = null,
                        modifier = Modifier.size(64.dp)
                    )
                    Text(
                        stringResource(R.string.there_s_no_items_in_the_list),
                        fontWeight = FontWeight.Normal,
                        fontSize = 22.sp
                    )
                }
            }
        }

    } ?: AnimatedVisibility(
        visible = !shoppingListState.isLoadingItems,
        exit = fadeOut(tween(100))
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.no_shopping_list_selected),
                    fontSize = 20.sp
                )
                if (windowSize == WindowSize.Compact) {
                    OutlinedButton(onClick = {
                        scope.launch {
                            drawerState.open()
                        }
                    }) {
                        Text(text = stringResource(R.string.select_one_shopping_list))
                    }
                }
            }
        }
    }
}