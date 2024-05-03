package dev.janssenbatista.shoppinglist.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import dev.janssenbatista.shoppinglist.R
import dev.janssenbatista.shoppinglist.data.database.daos.ShoppingListDao
import dev.janssenbatista.shoppinglist.data.entities.Item
import dev.janssenbatista.shoppinglist.data.enums.Colors
import dev.janssenbatista.shoppinglist.data.entities.ShoppingList

@Database(entities = [ShoppingList::class, Item::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun shoppingListDao(): ShoppingListDao
}

class DatabaseCallBack(private val context: Context) : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        val shoppingList = ShoppingList(
            description = context.getString(R.string.default_shopping_list),
            colorId = Colors.entries.shuffled()[0].id
        )
        db.execSQL("INSERT INTO tb_shopping_lists(description, color_id) VALUES ('${shoppingList.description}', ${shoppingList.colorId})")
    }
}
