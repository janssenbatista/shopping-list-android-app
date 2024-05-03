package dev.janssenbatista.shoppinglist.data.repositories.shoppinglist

import dev.janssenbatista.shoppinglist.data.database.daos.ShoppingListDao
import dev.janssenbatista.shoppinglist.data.entities.Item
import dev.janssenbatista.shoppinglist.data.entities.ShoppingList
import dev.janssenbatista.shoppinglist.data.entities.ShoppingListWithItems
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class RoomShoppingListRepository(private val dao: ShoppingListDao) : ShoppingListRepository {
    override suspend fun save(shoppingList: ShoppingList) {
        withContext(Dispatchers.IO) {
            dao.save(shoppingList)
        }
    }

    override suspend fun saveItem(item: Item) {
        withContext(Dispatchers.IO) {
            dao.saveItem(item)
        }
    }

    override fun getShoppingListWithItems(shoppingListId: Long): Flow<ShoppingListWithItems?> =
        dao.getShoppingListWithItems(shoppingListId)


    override fun getAll(): Flow<List<ShoppingList>> =
        dao.getAll()

    override fun findByDescription(description: String): Flow<ShoppingList?> =
        dao.findByDescription(description)


    override suspend fun findById(shoppingListId: Long): Flow<ShoppingList?> {
        return withContext(Dispatchers.IO) {
            dao.findById(shoppingListId)
        }
    }

    override suspend fun deleteShoppingListById(shoppingListId: Long) {
        withContext(Dispatchers.IO) {
            dao.deleteShoppingListById(shoppingListId)
        }
    }

    override suspend fun deleteItemsByShoppingListId(shoppingListId: Long) {
        withContext(Dispatchers.IO) {
            dao.deleteItemsByShoppingListId(shoppingListId)
        }
    }

    override suspend fun deleteItem(item: Item) {
        dao.deleteItem(item)
    }

    override fun getAllItemsByShoppingListId(shoppingListId: Long): Flow<List<Item>> {
        return dao.getAllItemsByShoppingListId(shoppingListId)
    }
}