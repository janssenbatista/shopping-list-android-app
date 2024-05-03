package dev.janssenbatista.shoppinglist.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tb_shopping_lists")
data class ShoppingList(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    @ColumnInfo(index = true)
    val description: String,
    @ColumnInfo(name = "color_id")
    val colorId: Int
)