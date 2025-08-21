package dev.janssenbatista.shoppinglist.ui.screens.shoppinglist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.janssenbatista.shoppinglist.R
import dev.janssenbatista.shoppinglist.ui.components.Cart
import dev.janssenbatista.shoppinglist.ui.components.DeleteAlertDialog
import dev.janssenbatista.shoppinglist.ui.components.ItemFormDialog
import dev.janssenbatista.shoppinglist.ui.components.ShoppingList
import dev.janssenbatista.shoppinglist.ui.components.ShoppingLists
import dev.janssenbatista.shoppinglist.ui.screens.shoppingListForm.ShoppingListFormScreen
import dev.janssenbatista.shoppinglist.ui.utils.WindowSize
import dev.janssenbatista.shoppinglist.ui.utils.getWindowSize
import kotlinx.coroutines.Job
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

        val windowSize = getWindowSize()

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

        LaunchedEffect(key1 = shoppingListState) {
            if (shoppingListState.itemsAtCart.isEmpty()) {
                isCartOpen = false
            }
        }

        val snackBarHostState = remember {
            SnackbarHostState()
        }


        when (windowSize) {
            WindowSize.Compact -> {
                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        ModalDrawerSheet {
                            ShoppingLists(shoppingListState, drawerState)
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
                                        contentDescription = if (drawerState.isClosed) stringResource(
                                            R.string.open_menu
                                        )
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
                                windowSize = windowSize,
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
            }

            WindowSize.Medium, WindowSize.Expanded -> {
                Row {
                    Box(Modifier.weight(2f)) {
                        ShoppingLists(shoppingListState, drawerState)
                    }
                    Scaffold(
                        modifier = Modifier.weight(4f),
                        topBar = {
                            TopAppBar(
                                title = {
                                    AnimatedVisibility(visible = shoppingListState.shoppingListWithItems != null) {
                                        Text(
                                            text = shoppingListState.shoppingListWithItems?.shoppingList?.description
                                                ?: ""
                                        )
                                    }
                                },
                                actions = {
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
                        Row(modifier = Modifier.padding(paddingValues)) {
                            Box(Modifier.weight(4f)) {
                                ShoppingList(
                                    shoppingListState = shoppingListState,
                                    selectedShoppingListId = selectedShoppingListId,
                                    windowSize = windowSize,
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

