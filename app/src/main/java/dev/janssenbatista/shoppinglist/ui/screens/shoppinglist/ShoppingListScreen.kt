package dev.janssenbatista.shoppinglist.ui.screens.shoppinglist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.janssenbatista.shoppinglist.R
import dev.janssenbatista.shoppinglist.data.enums.Colors
import dev.janssenbatista.shoppinglist.ui.components.Cart
import dev.janssenbatista.shoppinglist.ui.components.DeleteAlertDialog
import dev.janssenbatista.shoppinglist.ui.components.DrawMenuHeader
import dev.janssenbatista.shoppinglist.ui.components.ItemFormDialog
import dev.janssenbatista.shoppinglist.ui.components.ShoppingList
import dev.janssenbatista.shoppinglist.ui.screens.shoppingListForm.ShoppingListFormScreen
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

object ShoppingListScreen : Screen {
    private fun readResolve(): Any = ShoppingListScreen

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Open)
        val viewModel: ShoppingListViewModel = koinViewModel()
        val shoppingListState by viewModel.shoppingListState.collectAsState()
        val itemState by viewModel.itemState.collectAsState()
        val selectedShoppingListId by viewModel.selectedShoppingListId.collectAsState()
        val scope = rememberCoroutineScope()
        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current
        var snackBarJob by remember { mutableStateOf<Job?>(null) }

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

        var isCartOpen by remember {
            mutableStateOf(false)
        }

        val searchInputFocusRequester = remember {
            FocusRequester()
        }

        LaunchedEffect(key1 = shoppingListState) {
            if (shoppingListState.itemsAtCart.isEmpty()) {
                isCartOpen = false
            }
        }

        val snackBarHostState = remember {
            SnackbarHostState()
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
            }) {
            Scaffold(
                topBar = {
                    TopAppBar(title = {
                        AnimatedVisibility(visible = shoppingListState.shoppingListWithItems != null) {
                            Text(
                                text = shoppingListState.shoppingListWithItems?.shoppingList?.description
                                    ?: ""
                            )
                        }
                    }, navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                drawerState.apply {
                                    if (isClosed) open() else close()
                                }
                            }.invokeOnCompletion {
                                snackBarJob?.cancel()
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Menu,
                                contentDescription = if (drawerState.isClosed) stringResource(R.string.open_menu)
                                else stringResource(
                                    R.string.close_menu
                                )
                            )
                        }
                    }, actions = {
                        AnimatedVisibility(
                            visible = shoppingListState.itemsAtCart.isNotEmpty(),
                            enter = fadeIn() + scaleIn(),
                            exit = fadeOut() + scaleOut()
                        ) {
                            BadgedBox(
                                modifier = Modifier
                                    .padding(end = 12.dp)
                                    .clickable {
                                        isCartOpen = true
                                        snackBarJob?.cancel()
                                    },
                                badge = {
                                    Badge {
                                        Text(text = shoppingListState.itemsAtCart.size.toString())
                                    }
                                }
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_shopping_cart),
                                    contentDescription = null
                                )
                            }

                        }
                        shoppingListState.items.let { items ->
                            selectedShoppingListId?.let {
                                AnimatedVisibility(visible = !shoppingListState.isLoadingItems) {
                                    IconButton(onClick = {
                                        isPopupMenuVisible = !isPopupMenuVisible
                                    }) {
                                        Icon(
                                            imageVector = Icons.Filled.MoreVert,
                                            contentDescription = stringResource(R.string.open_popup_menu)
                                        )
                                    }
                                }
                            }
                            DropdownMenu(
                                expanded = isPopupMenuVisible,
                                onDismissRequest = { isPopupMenuVisible = false }) {
                                selectedShoppingListId?.let {
                                    DropdownMenuItem(
                                        text = { Text(text = stringResource(R.string.edit_shopping_list)) },
                                        onClick = {
                                            isPopupMenuVisible = false
                                            navigator.push(ShoppingListFormScreen(it))
                                        }
                                    )
                                }
                                if (items.isNotEmpty()) {
                                    DropdownMenuItem(text = {
                                        Text(text = stringResource(R.string.delete_all_items))
                                    }, onClick = {
                                        isDeleteAllItemsDialogVisible = true
                                    })
                                    DropdownMenuItem(text = {
                                        Text(text = stringResource(R.string.add_all_to_cart))
                                    }, onClick = {
                                        viewModel.addAllToCart(selectedShoppingListId!!)
                                        isPopupMenuVisible = false
                                    })
                                }
                                DropdownMenuItem(text = {
                                    Text(text = stringResource(R.string.delete_shopping_list))
                                }, onClick = {
                                    isDeleteShoppingListDialogVisible = true
                                })
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
                },
                snackbarHost = {
                    SnackbarHost(hostState = snackBarHostState)
                }
            ) { paddingValues ->

                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(paddingValues = paddingValues),
                ) {
                    ShoppingList(
                        shoppingListState = shoppingListState,
                        selectedShoppingListId = selectedShoppingListId,
                        drawerState = drawerState,
                        onEditItem = { item ->
                            itemState.apply {
                                onNameChange(item.name)
                                onQuantityChange(item.quantity.toString())
                                onUnitChange(item.unit)
                                onIsInTheCartChange(item.isInTheCart)
                            }
                            isUpdatingItem = true
                            isItemFormDialogOpen = true
                        },
                        onDeleteItem = { item ->
                            itemState.onDeleteItem(item)
                            snackBarJob?.cancel()
                            snackBarJob = scope.launch {
                                val result = snackBarHostState.showSnackbar(
                                    message = context.getString(
                                        R.string.item_removed,
                                        item.name
                                    ),
                                    duration = SnackbarDuration.Short,
                                    withDismissAction = true,
                                    actionLabel = context.getString(R.string.undo)
                                )
                                when (result) {
                                    SnackbarResult.ActionPerformed -> {
                                        itemState.onSaveItem(
                                            item.copy(isInTheCart = false)
                                        )
                                    }

                                    SnackbarResult.Dismissed -> {}
                                }
                            }
                        },
                        onItemChecked = { item ->
                            snackBarJob?.cancel()
                            snackBarJob = scope.launch {
                                val result = snackBarHostState.showSnackbar(
                                    message = context.getString(
                                        R.string.added_to_cart,
                                        item.name
                                    ),
                                    duration = SnackbarDuration.Short,
                                    withDismissAction = true,
                                    actionLabel = context.getString(R.string.undo)
                                )
                                when (result) {
                                    SnackbarResult.ActionPerformed -> {
                                        itemState.onSaveItem(
                                            item.copy(isInTheCart = false)
                                        )
                                    }

                                    SnackbarResult.Dismissed -> {}
                                }
                            }
                            itemState.onSaveItem(
                                item.copy(
                                    isInTheCart = true
                                )
                            )
                        }
                    )
                    if (shoppingListState.isLoadingItems) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(Modifier.animateContentSize())
                        }
                    }
                    Cart(
                        itemsAtCart = shoppingListState.itemsAtCart,
                        itemState = itemState,
                        onEditItem = { item ->
                            itemState.apply {
                                onNameChange(item.name)
                                onQuantityChange(item.quantity.toString())
                                onUnitChange(item.unit)
                                onIsInTheCartChange(item.isInTheCart)
                            }
                            isUpdatingItem = true
                            isItemFormDialogOpen = true
                        },
                        isCartOpen = isCartOpen
                    ) { isCartOpen = false }
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
                    isPopupMenuVisible = false
                },
                onConfirm = {
                    scope.launch {
                        drawerState.open()
                    }
                    isPopupMenuVisible = false
                    isDeleteShoppingListDialogVisible = false
                    viewModel.deleteShoppingListAndItems(
                        shoppingListState.shoppingListWithItems!!.shoppingList.id!!
                    )
                })
        }
        if (isDeleteAllItemsDialogVisible) {
            DeleteAlertDialog(
                title = stringResource(R.string.are_you_sure_you_want_to_delete_all_items),
                onDismiss = {
                    isPopupMenuVisible = false
                    isDeleteAllItemsDialogVisible = false
                }, onConfirm = {
                    isPopupMenuVisible = false
                    isDeleteAllItemsDialogVisible = false
                    viewModel.deleteAllItems(selectedShoppingListId!!)
                })
        }

    }
}

