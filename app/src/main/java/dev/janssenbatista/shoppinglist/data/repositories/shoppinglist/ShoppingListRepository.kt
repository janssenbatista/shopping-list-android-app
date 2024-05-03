package dev.janssenbatista.shoppinglist.data.repositories.shoppinglist

import dev.janssenbatista.shoppinglist.data.entities.Item
import dev.janssenbatista.shoppinglist.data.entities.ShoppingList
import dev.janssenbatista.shoppinglist.data.entities.ShoppingListWithItems
import kotlinx.coroutines.flow.Flow

interface ShoppingListRepository {
    suspend fun save(shoppingList: ShoppingList)

    suspend fun saveItem(item: Item)

    fun getShoppingListWithItems(shoppingListId: Long): Flow<ShoppingListWithItems?>

    fun getAll(): Flow<List<ShoppingList>>

    fun findByDescription(description: String): Flow<ShoppingList?>

    suspend fun findById(shoppingListId: Long): Flow<ShoppingList?>

    suspend fun deleteShoppingListById(shoppingListId: Long)

    suspend fun deleteItemsByShoppingListId(shoppingListId: Long)

    suspend fun deleteItem(item: Item)

    fun getAllItemsByShoppingListId(shoppingListId: Long): Flow<List<Item>>
}