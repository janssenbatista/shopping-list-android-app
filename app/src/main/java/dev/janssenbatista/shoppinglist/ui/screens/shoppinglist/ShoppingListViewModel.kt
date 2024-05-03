package dev.janssenbatista.shoppinglist.ui.screens.shoppinglist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.janssenbatista.shoppinglist.data.entities.Item
import dev.janssenbatista.shoppinglist.data.entities.ShoppingList
import dev.janssenbatista.shoppinglist.data.repositories.shoppinglist.ShoppingListRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ShoppingListViewModel(
    private val shoppingListRepository: ShoppingListRepository,
) : ViewModel() {

    private val _shoppingListState = MutableStateFlow(ShoppingListState())
    val shoppingListState = _shoppingListState.asStateFlow()

    private val _itemState = MutableStateFlow(ItemState())
    val itemState = _itemState.asStateFlow()

    private val _isLoadingItems = MutableStateFlow(false)
    val isLoadingItems = _isLoadingItems.asStateFlow()

    init {
        viewModelScope.launch {
            shoppingListRepository.getAll().collect { shoppingList ->
                _shoppingListState.update {
                    it.copy(shoppingLists = shoppingList)
                }
            }
        }

        _itemState.update { currentState ->
            currentState.copy(
                onDeleteItem = { item ->
                    viewModelScope.launch {
                        shoppingListRepository.deleteItem(item)
                        itemState.value.refreshItemsList()
                    }
                },
                refreshItemsList = {
                    viewModelScope.launch {
                        _shoppingListState.value.selectedShoppingList?.id?.let {
                            shoppingListRepository.getAllItemsByShoppingListId(it)
                                .flowOn(Dispatchers.IO).collect { items ->
                                    _itemState.update { state ->
                                        state.copy(items = items)
                                    }
                                }
                        }
                    }
                },
                onNameChange = { name ->
                    _itemState.update {
                        it.copy(name = name)
                    }
                },
                onQuantityChange = { quantity ->
                    _itemState.update {
                        it.copy(quantity = quantity)
                    }
                },
                onUnitChange = { unit ->
                    _itemState.update {
                        it.copy(unit = unit)
                    }
                },
                onIsInTheCartChange = { isInTheCart ->
                    _itemState.update {
                        it.copy(isInTheCart = isInTheCart)
                    }
                },
                onSaveItem = { item ->
                    viewModelScope.launch {
                        shoppingListRepository.saveItem(item)
                        itemState.value.refreshItemsList()
                    }
                },
                clearFields = {
                    _itemState.update {
                        it.copy(name = "", quantity = "", unit = "")
                    }
                })

        }

        _shoppingListState.update { shoppingListState ->
            shoppingListState.copy(
                onSearchShoppingList = { descriptionContains ->
                    val filteredShoppingLists =
                        _shoppingListState.value.shoppingLists.filter { shoppingList ->
                            shoppingList.description.contains(
                                descriptionContains,
                                ignoreCase = true
                            )
                        }
                    _shoppingListState.update {
                        it.copy(filteredShoppingLists = filteredShoppingLists)
                    }
                },
                onClearSearch = {
                    _shoppingListState.update {
                        it.copy(filteredShoppingLists = emptyList(), selectedShoppingList = null, descriptionContains = "")
                    }
                },
                onSelectShoppingList = { id ->
                    val selectedShoppingList = _shoppingListState.value.selectedShoppingList
                    if (selectedShoppingList != null && selectedShoppingList.id == id) {
                        return@copy
                    }
                    _itemState.value.items = emptyList()
                    _isLoadingItems.value = true
                    viewModelScope.launch {
                        val shoppingList = shoppingListRepository.findById(id)
                            .flowOn(Dispatchers.IO)
                            .first()
                        _shoppingListState.update {
                            it.copy(
                                selectedShoppingList = shoppingList,
                                shoppingListId = shoppingList?.id ?: -1
                            )
                        }
                        if (shoppingList != null) {
                            val items =
                                shoppingListRepository.getAllItemsByShoppingListId(shoppingList.id!!)
                                    .flowOn(Dispatchers.IO)
                                    .onStart { delay(100) }
                                    .first()
                            _itemState.update {
                                it.copy(items = items)
                            }
                            _isLoadingItems.value = false
                        }
                    }
                },
                onDescriptionContainsChange = {description ->
                    _shoppingListState.update {
                        it.copy(descriptionContains = description)
                    }
                }
            )
        }


    }

    fun deleteShoppingListAndItems(shoppingListId: Long) {
        viewModelScope.launch {
            shoppingListRepository.apply {
                deleteItemsByShoppingListId(shoppingListId)
            }
        }
        viewModelScope.launch {
            shoppingListRepository.deleteShoppingListById(shoppingListId)
            _shoppingListState.update {
                it.copy(selectedShoppingList = null, filteredShoppingLists = emptyList())
            }
        }
    }

    fun deleteAllItems(shoppingListId: Long) {
        viewModelScope.launch {
            shoppingListRepository.deleteItemsByShoppingListId(shoppingListId)
            itemState.value.refreshItemsList()
        }
    }
}

data class ShoppingListState(
    var shoppingLists: List<ShoppingList> = emptyList(),
    var shoppingListId: Long? = null,
    var selectedShoppingList: ShoppingList? = null,
    var filteredShoppingLists: List<ShoppingList> = emptyList(),
    var descriptionContains: String = "",
    var onSearchShoppingList: (String) -> Unit = {},
    var onClearSearch: () -> Unit = {},
    var onSelectShoppingList: (Long) -> Unit = {},
    var onDescriptionContainsChange: (String) -> Unit = {}
)

data class ItemState(
    var name: String = "",
    var quantity: String = "",
    var unit: String = "",
    var isInTheCart: Boolean = false,
    var items: List<Item> = emptyList(),
    var inputErrorMessage: String = "",
    var onNameChange: (String) -> Unit = {},
    var onQuantityChange: (String) -> Unit = {},
    var onUnitChange: (String) -> Unit = {},
    var onIsInTheCartChange: (Boolean) -> Unit = {},
    var onDeleteItem: (Item) -> Unit = {},
    var onSaveItem: (Item) -> Unit = {},
    var clearFields: () -> Unit = {},
    var refreshItemsList: () -> Unit = {}
)