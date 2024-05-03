package dev.janssenbatista.shoppinglist.data.entities

import androidx.room.Embedded
import androidx.room.Relation

data class ShoppingListWithItems(
    @Embedded
    val shoppingList: ShoppingList,
    @Relation(parentColumn = "id", entityColumn = "shopping_list_id")
    val items: List<Item>
)
