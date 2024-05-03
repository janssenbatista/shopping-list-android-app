package dev.janssenbatista.shoppinglist.di

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import dev.janssenbatista.shoppinglist.data.database.AppDatabase
import dev.janssenbatista.shoppinglist.data.database.DatabaseCallBack
import dev.janssenbatista.shoppinglist.data.repositories.shoppinglist.RoomShoppingListRepository
import dev.janssenbatista.shoppinglist.data.repositories.shoppinglist.ShoppingListRepository
import dev.janssenbatista.shoppinglist.ui.screens.shoppingListForm.ShoppingListFormViewModel
import dev.janssenbatista.shoppinglist.ui.screens.shoppinglist.ShoppingListViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    viewModelOf(::ShoppingListViewModel)
    viewModelOf(::ShoppingListFormViewModel)
}

val dataModule = module {
    single<AppDatabase> {
        Room.databaseBuilder(
            androidApplication(),
            AppDatabase::class.java,
            "shopping_list.db"
        )
            .addCallback(DatabaseCallBack(androidApplication()))
            .build()
    }

    single {
        get<AppDatabase>().shoppingListDao()
    }

    single<ShoppingListRepository> {
        RoomShoppingListRepository(get())
    }
    single {
        PreferenceDataStoreFactory.create {
            androidContext()
                .preferencesDataStoreFile("app_preferences")
        }
    }
}