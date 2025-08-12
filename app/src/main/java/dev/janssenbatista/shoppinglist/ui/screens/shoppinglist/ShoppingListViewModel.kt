package dev.janssenbatista.shoppinglist.ui.screens.shoppinglist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.janssenbatista.shoppinglist.data.entities.Item
import dev.janssenbatista.shoppinglist.data.entities.ShoppingList
import dev.janssenbatista.shoppinglist.data.entities.ShoppingListWithItems
import dev.janssenbatista.shoppinglist.data.repositories.shoppinglist.ShoppingListRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class ShoppingListViewModel(
    private val shoppingListRepository: ShoppingListRepository,
) : ViewModel() {

    private val _shoppingListState = MutableStateFlow(ShoppingListState())
    val shoppingListState = _shoppingListState.asStateFlow()

    private val _itemState = MutableStateFlow(ItemState())
    val itemState = _itemState.asStateFlow()

    private val _selectedShoppingListId = MutableStateFlow<Long?>(null)
    val selectedShoppingListId = _selectedShoppingListId.asStateFlow()

    init {
        viewModelScope.launch {
            shoppingListRepository.getAll().collect { shoppingLists ->
                _shoppingListState.update {
                    it.copy(shoppingLists = shoppingLists)
                }
            }
        }

        viewModelScope.launch {
            _selectedShoppingListId
                .filterNotNull()
                .flatMapLatest { id ->
                    shoppingListRepository.getShoppingListWithItems(id)
                }.catch {
                    _shoppingListState.update {
                        it.copy(isLoadingItems = false)
                    }
                }
                .collect { shoppingListWithItems ->
                    shoppingListWithItems?.let {
                        val items = shoppingListWithItems.items.filter { item -> !item.isInTheCart }
                        val itemsAtCart =
                            shoppingListWithItems.items.filter { item -> item.isInTheCart }
                        _shoppingListState.update {
                            it.copy(
                                shoppingListWithItems = shoppingListWithItems,
                                items = items,
                                itemsAtCart = itemsAtCart,
                                isLoadingItems = false
                            )
                        }
                    }
                }
        }


        _itemState.update { currentState ->
            currentState.copy(
                onDeleteItem = { item ->
                    viewModelScope.launch {
                        shoppingListRepository.deleteItem(item)
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
                        it.copy(
                            filteredShoppingLists = emptyList(),
                            descriptionContains = ""
                        )
                    }
                },
                onSelectShoppingList = { id ->
                    if (_selectedShoppingListId.value == id) {
                        return@copy
                    }
                    _shoppingListState.update {
                        it.copy(
                            isLoadingItems = true,
                            shoppingListWithItems = null,
                            items = emptyList(),
                            itemsAtCart = emptyList()
                        )
                    }
                    viewModelScope.launch {
                        delay(timeMillis = 1_000)
                        _selectedShoppingListId.value = id
                    }
                },
                onDescriptionContainsChange = { description ->
                    _shoppingListState.update {
                        it.copy(descriptionContains = description)
                    }
                }
            )
        }
    }

    fun deleteShoppingListAndItems(shoppingListId: Long) {
        viewModelScope.launch {
            shoppingListRepository.deleteItemsByShoppingListId(shoppingListId)
        }
        viewModelScope.launch {
            shoppingListRepository.deleteShoppingListById(shoppingListId)
            _shoppingListState.update {
                it.copy(filteredShoppingLists = emptyList(), itemsAtCart = emptyList())
            }
            if (_selectedShoppingListId.value == shoppingListId) {
                _selectedShoppingListId.value = null
                _shoppingListState.update { it.copy(shoppingListWithItems = null) }
            }
        }
    }

    fun addAllToCart(shoppingListId: Long) {
        viewModelScope.launch {
            shoppingListRepository.addAllToCart(shoppingListId)
        }
    }

    fun deleteAllItems(shoppingListId: Long) {
        viewModelScope.launch {
            shoppingListRepository.deleteItemsByShoppingListId(shoppingListId)
        }.invokeOnCompletion {
            _shoppingListState.update {
                it.copy(filteredShoppingLists = emptyList(), itemsAtCart = emptyList())
            }
        }
    }
}

data class ShoppingListState(
    val shoppingLists: List<ShoppingList> = emptyList(),
    val shoppingListWithItems: ShoppingListWithItems? = null,
    val items: List<Item> = emptyList(),
    val itemsAtCart: List<Item> = emptyList(),
    val isLoadingItems: Boolean = false,
    val filteredShoppingLists: List<ShoppingList> = emptyList(),
    val descriptionContains: String = "",
    val onSearchShoppingList: (String) -> Unit = {},
    val onClearSearch: () -> Unit = {},
    val onSelectShoppingList: (Long) -> Unit = {},
    val onDescriptionContainsChange: (String) -> Unit = {}
)

data class ItemState(
    val name: String = "",
    val quantity: String = "",
    val unit: String = "",
    val isInTheCart: Boolean = false,
    val inputErrorMessage: String = "",
    val onNameChange: (String) -> Unit = {},
    val onQuantityChange: (String) -> Unit = {},
    val onUnitChange: (String) -> Unit = {},
    val onIsInTheCartChange: (Boolean) -> Unit = {},
    val onDeleteItem: (Item) -> Unit = {},
    val onSaveItem: (Item) -> Unit = {},
    val clearFields: () -> Unit = {},
)