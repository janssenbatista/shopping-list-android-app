package dev.janssenbatista.shoppinglist.data.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import dev.janssenbatista.shoppinglist.data.entities.Item
import dev.janssenbatista.shoppinglist.data.entities.ShoppingList
import dev.janssenbatista.shoppinglist.data.entities.ShoppingListWithItems
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingListDao {

    @Upsert
    suspend fun save(shoppingList: ShoppingList)

    @Upsert
    suspend fun saveItem(item: Item)

    @Transaction
    @Query("SELECT * FROM tb_shopping_lists WHERE id = :shoppingListId")
    fun getShoppingListWithItems(shoppingListId: Long): Flow<ShoppingListWithItems?>

    @Query("SELECT * FROM tb_shopping_lists ORDER BY description")
    fun getAll(): Flow<List<ShoppingList>>

    @Query("SELECT * FROM tb_shopping_lists WHERE description = :description")
    fun findByDescription(description: String): Flow<ShoppingList?>

    @Query("SELECT * FROM tb_shopping_lists WHERE id = :shoppingListId")
    fun findById(shoppingListId: Long): Flow<ShoppingList?>

    @Query("DELETE FROM tb_shopping_lists WHERE id = :shoppingListId")
    suspend fun deleteShoppingListById(shoppingListId: Long)

    @Query("DELETE FROM tb_items WHERE shopping_list_id = :shoppingListId")
    suspend fun deleteItemsByShoppingListId(shoppingListId: Long)

    @Delete
    suspend fun deleteItem(item: Item)

    @Query("SELECT * FROM tb_items WHERE shopping_list_id = :shoppingListId ORDER BY is_in_the_cart")
    fun getAllItemsByShoppingListId(shoppingListId: Long): Flow<List<Item>>
}