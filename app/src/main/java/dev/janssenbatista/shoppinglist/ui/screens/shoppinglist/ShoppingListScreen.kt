package dev.janssenbatista.shoppinglist.ui.screens.shoppinglist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.janssenbatista.shoppinglist.R
import dev.janssenbatista.shoppinglist.data.enums.Colors
import dev.janssenbatista.shoppinglist.ui.components.DeleteAlertDialog
import dev.janssenbatista.shoppinglist.ui.components.DrawMenuHeader
import dev.janssenbatista.shoppinglist.ui.components.ItemFormDialog
import dev.janssenbatista.shoppinglist.ui.components.ShoppingListItem
import dev.janssenbatista.shoppinglist.ui.screens.shoppingListForm.ShoppingListFormScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

object ShoppingListScreen : Screen {
    private fun readResolve(): Any = ShoppingListScreen

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
    @Composable
    override fun Content() {
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Open)
        val viewModel: ShoppingListViewModel = koinViewModel()
        val shoppingListState by viewModel.shoppingListState.collectAsState()
        val itemState by viewModel.itemState.collectAsState()
        val isLoadingItems by viewModel.isLoadingItems.collectAsState()
        val scope = rememberCoroutineScope()
        val navigator = LocalNavigator.currentOrThrow

        var isSearchBarVisible by remember {
            mutableStateOf(false)
        }

        var isItemFormDialogOpen by remember {
            mutableStateOf(false)
        }

        var isDeleteShoppingListDialogVisible by remember {
            mutableStateOf(false)
        }

        var isDeleteAllItemsDialogVisible by remember {
            mutableStateOf(false)
        }

        var isPopupMenuVisible by remember {
            mutableStateOf(false)
        }

        var isUpdatingItem by remember {
            mutableStateOf(false)
        }

        val searchInputFocusRequester = remember {
            FocusRequester()
        }

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
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
                                if (shoppingListState.shoppingLists.size >= 5) {
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
                                    onClick = { navigator.push(ShoppingListFormScreen) },
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
            }) {
            Scaffold(
                topBar = {
                    TopAppBar(title = {
                        Text(
                            text = shoppingListState.shoppingListWithItems?.shoppingList?.description
                                ?: ""
                        )
                    }, navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                drawerState.apply {
                                    if (isClosed) open() else close()
                                }
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Menu,
                                contentDescription = if (drawerState.isClosed) stringResource(R.string.open_menu) else stringResource(
                                    R.string.close_menu
                                )
                            )
                        }
                    }, actions = {
                        shoppingListState.shoppingListWithItems?.let {
                            IconButton(onClick = {
                                isDeleteShoppingListDialogVisible = true
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = stringResource(R.string.delete_shopping_list)
                                )
                            }
                        }
                        shoppingListState.shoppingListWithItems?.let {
                            if (it.items.isNotEmpty()) {
                                IconButton(onClick = { isPopupMenuVisible = !isPopupMenuVisible }) {
                                    Icon(
                                        imageVector = Icons.Filled.MoreVert,
                                        contentDescription = stringResource(R.string.open_popup_menu)
                                    )
                                }
                                DropdownMenu(
                                    expanded = isPopupMenuVisible,
                                    onDismissRequest = { isPopupMenuVisible = false }) {

                                    DropdownMenuItem(text = {
                                        Text(text = stringResource(R.string.delete_all_items))
                                    }, onClick = {
                                        isDeleteAllItemsDialogVisible = true
                                    })
                                }
                            }
                        }
                    })
                },
                floatingActionButton = {
                    shoppingListState.shoppingListWithItems?.let {
                        FloatingActionButton(onClick = { isItemFormDialogOpen = true }) {
                            Icon(
                                Icons.Filled.Add,
                                contentDescription = stringResource(R.string.add_new_item_button)
                            )
                        }
                    }
                }
            ) { paddingValues ->
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(paddingValues = paddingValues),
                ) {

                    shoppingListState.shoppingListWithItems?.let { shoppingListWithItems ->
                        AnimatedVisibility(visible = true) {
                            LazyColumn {
                                itemsIndexed(
                                    items = shoppingListWithItems.items,
                                    key = { _, item ->
                                        item.name
                                    }) { index, item ->
                                    ShoppingListItem(
                                        Modifier
                                            .padding(horizontal = 16.dp, vertical = 8.dp)
                                            .clickable {
                                                itemState.onSaveItem(
                                                    item.copy(
                                                        isInTheCart = !item.isInTheCart
                                                    )
                                                )
                                            }
                                            .animateItemPlacement(tween(300))
                                            .fillMaxWidth(),
                                        item = item,
                                        onDeleteItem = {
                                            itemState.onDeleteItem(item)
                                        },
                                        onEditItem = {
                                            itemState.apply {
                                                onNameChange(item.name)
                                                onQuantityChange(item.quantity.toString())
                                                onUnitChange(item.unit)
                                                onIsInTheCartChange(item.isInTheCart)
                                            }
                                            isUpdatingItem = true
                                            isItemFormDialogOpen = true
                                        },
                                        onItemChecked = {
                                            itemState.onSaveItem(
                                                item.copy(
                                                    isInTheCart = !item.isInTheCart
                                                )
                                            )
                                        })
                                    if (index < itemState.items.size - 1) {
                                        HorizontalDivider()
                                    }
                                }
                            }
                            AnimatedVisibility(
                                visible = isLoadingItems,
                                enter = fadeIn() + scaleIn(tween(300)),
                                exit = fadeOut() + scaleOut(tween(300))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize(), contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }
                    } ?: Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.no_shopping_list_selected),
                                fontSize = 20.sp
                            )
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
            if (isItemFormDialogOpen) {
                shoppingListState.shoppingListWithItems?.shoppingList?.let {
                    ItemFormDialog(
                        shoppingListId = it.id!!,
                        onDismiss = {
                            isItemFormDialogOpen = false
                            itemState.clearFields()
                            isUpdatingItem = false
                        },
                        itemState = itemState,
                        isUpdating = isUpdatingItem
                    )
                }
            }
            if (isDeleteShoppingListDialogVisible) {
                DeleteAlertDialog(
                    title = stringResource(id = R.string.delete_shopping_list),
                    text = stringResource(
                        R.string.are_you_sure_you_want_to_delete_shopping_list,
                        shoppingListState.shoppingListWithItems?.shoppingList!!.description
                    ),
                    onDismiss = {
                        isDeleteShoppingListDialogVisible = false
                    },
                    onConfirm = {
                        scope.launch {
                            drawerState.open()
                        }
                        isDeleteShoppingListDialogVisible = false
                        viewModel.deleteShoppingListAndItems(shoppingListState.shoppingListWithItems!!.shoppingList.id!!)
                    })
            }
            if (isDeleteAllItemsDialogVisible) {
                DeleteAlertDialog(
                    title = stringResource(R.string.are_you_sure_you_want_to_delete_all_items),
                    onDismiss = {
                        isDeleteAllItemsDialogVisible = false
                        isPopupMenuVisible = false
                    }, onConfirm = {
                        shoppingListState.shoppingListWithItems?.shoppingList?.let {
                            viewModel.deleteAllItems(it.id!!)
                            isPopupMenuVisible = false
                            isDeleteAllItemsDialogVisible = false
                        }
                    })
            }

        }
    }
}
