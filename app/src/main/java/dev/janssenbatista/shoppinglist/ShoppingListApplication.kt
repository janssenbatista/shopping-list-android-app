package dev.janssenbatista.shoppinglist

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class ShoppingListApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@ShoppingListApplication)
        }
    }
}