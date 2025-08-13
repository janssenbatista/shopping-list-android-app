package dev.janssenbatista.shoppinglist.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.janssenbatista.shoppinglist.R
import dev.janssenbatista.shoppinglist.data.enums.Colors
import dev.janssenbatista.shoppinglist.ui.screens.shoppingListForm.ShoppingListFormScreen
import dev.janssenbatista.shoppinglist.ui.screens.shoppinglist.ShoppingListState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ShoppingLists(shoppingListState: ShoppingListState, drawerState: DrawerState) {

    val navigator = LocalNavigator.currentOrThrow
    val scope = rememberCoroutineScope()

    var isSearchBarVisible by remember {
        mutableStateOf(false)
    }

    val searchInputFocusRequester = remember {
        FocusRequester()
    }

    Column {
        DrawMenuHeader()
        Row(
            Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.shopping_lists),
                modifier = Modifier.weight(1f),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Row {
                if (shoppingListState.shoppingLists.size >= 3) {
                    IconButton(
                        onClick = {
                            isSearchBarVisible = !isSearchBarVisible
                        },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = if (isSearchBarVisible) {
                                if (isSystemInDarkTheme()) Color.White.copy(alpha = 0.2f) else Color.Black.copy(
                                    alpha = 0.2f
                                )
                            } else {
                                Color.Transparent
                            }
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = stringResource(R.string.search_shopping_list_button)
                        )
                    }
                }
                IconButton(
                    onClick = { navigator.push(ShoppingListFormScreen()) },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = stringResource(R.string.add_new_shopping_list_button)
                    )
                }
            }
        }
        AnimatedVisibility(visible = isSearchBarVisible) {
            OutlinedTextField(
                value = shoppingListState.descriptionContains,
                onValueChange = {
                    shoppingListState.apply {
                        onDescriptionContainsChange(it)
                        onSearchShoppingList(it)
                    }
                },
                modifier = Modifier
                    .padding(8.dp)
                    .padding(bottom = 16.dp)
                    .fillMaxWidth()
                    .focusRequester(searchInputFocusRequester),
                trailingIcon = {
                    IconButton(onClick = {
                        isSearchBarVisible = false
                        shoppingListState.onClearSearch()
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = stringResource(R.string.close_search_bar)
                        )
                    }
                })
            LaunchedEffect(key1 = isSearchBarVisible) {
                delay(300)
                if (isSearchBarVisible) {
                    searchInputFocusRequester.requestFocus()
                } else {
                    searchInputFocusRequester.freeFocus()
                }
            }
        }
        HorizontalDivider()
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            itemsIndexed(
                shoppingListState.filteredShoppingLists.ifEmpty { shoppingListState.shoppingLists }
            ) { index, list ->
                Column(
                    Modifier.background(
                        color = if (shoppingListState.shoppingListWithItems?.shoppingList?.id == list.id) {
                            if (isSystemInDarkTheme()) {
                                Color.White.copy(alpha = 0.1f)
                            } else {
                                Color.Black.copy(alpha = 0.1f)
                            }
                        } else Color.Transparent
                    )
                ) {
                    Row(
                        Modifier
                            .clickable {
                                scope
                                    .launch {
                                        drawerState.close()
                                    }
                                    .invokeOnCompletion {
                                        shoppingListState.onSelectShoppingList(list.id!!)
                                    }
                            }
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Canvas(Modifier.size(20.dp)) {
                            drawCircle(
                                color = Colors.getColorById(list.colorId).color
                            )
                        }
                        Text(
                            text = list.description, modifier = Modifier
                                .fillMaxSize()
                                .padding(vertical = 16.dp)
                        )
                    }
                    if (index < shoppingListState.shoppingLists.size - 1) {
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}