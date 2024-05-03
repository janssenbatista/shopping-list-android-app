package dev.janssenbatista.shoppinglist.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import java.util.Date

@Entity(tableName = "tb_items", primaryKeys = ["shopping_list_id", "name"])
data class Item(
    @ColumnInfo(name = "shopping_list_id")
    val shoppingListId: Long,
    val name: String,
    val unit: String,
    val quantity: Double,
    @ColumnInfo(name = "is_in_the_cart")
    val isInTheCart: Boolean = false,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = Date().time
)
