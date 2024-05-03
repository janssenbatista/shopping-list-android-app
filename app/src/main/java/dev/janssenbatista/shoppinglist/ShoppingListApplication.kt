package dev.janssenbatista.shoppinglist

import android.app.Application
import dev.janssenbatista.shoppinglist.di.appModule
import dev.janssenbatista.shoppinglist.di.dataModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class ShoppingListApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@ShoppingListApplication)
            modules(dataModule, appModule)
        }
    }
}