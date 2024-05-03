package dev.janssenbatista.shoppinglist.ui.screens.shoppingListForm

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cafe.adriel.voyager.navigator.Navigator
import dev.janssenbatista.shoppinglist.data.enums.Colors
import dev.janssenbatista.shoppinglist.data.entities.ShoppingList
import dev.janssenbatista.shoppinglist.data.repositories.shoppinglist.ShoppingListRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ShoppingListFormViewModel(private val repository: ShoppingListRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(UIState())
    val uiState = _uiState.asStateFlow()

    init {
        _uiState.update { currentState ->
            val color = Colors.entries.shuffled()[0]
            currentState.copy(
                shoppingListColor = color.color,
                shoppingListColorId = color.id,
                onShoppingListDescriptionChange = { description ->
                    _uiState.update {
                        it.copy(shoppingListDescription = description)
                    }
                },
                onShoppingListColorChange = { shoppingListColor ->
                    _uiState.update {
                        it.copy(shoppingListColor = shoppingListColor)
                    }
                },
                onShoppingListColorIdChange = { colorId ->
                    _uiState.update {
                        it.copy(shoppingListColorId = colorId)
                    }
                },
                setColorPickerVisible = { isColorPickerVisible ->
                    _uiState.update {
                        it.copy(isColorPickerVisible = isColorPickerVisible)
                    }
                },
                setShoppingListExists = { shoppingListExists ->
                    _uiState.update {
                        it.copy(shoppingListExists = shoppingListExists)
                    }
                }
            )
        }
    }

    fun save(navigator: Navigator) {
        viewModelScope.launch {
            val shoppingListExists: ShoppingList? =
                repository.findByDescription(description = uiState.value.shoppingListDescription)
                    .flowOn(Dispatchers.IO).first()
            if (shoppingListExists != null) {
                _uiState.update {
                    it.copy(
                        shoppingListExists = true
                    )
                }
                return@launch
            }
            val shoppingList = ShoppingList(
                description = uiState.value.shoppingListDescription,
                colorId = uiState.value.shoppingListColorId
            )
            repository.save(shoppingList)
            navigator.pop()
        }
    }


}

data class UIState(
    var shoppingListDescription: String = "",
    var shoppingListColor: Color = Color.Transparent,
    var shoppingListColorId: Int = -1,
    var isColorPickerVisible: Boolean = false,
    val shoppingListExists: Boolean = false,
    val onShoppingListDescriptionChange: (String) -> Unit = {},
    val onShoppingListColorChange: (Color) -> Unit = {},
    val onShoppingListColorIdChange: (Int) -> Unit = {},
    val setColorPickerVisible: (Boolean) -> Unit = {},
    val setShoppingListExists: (Boolean) -> Unit = {},
)