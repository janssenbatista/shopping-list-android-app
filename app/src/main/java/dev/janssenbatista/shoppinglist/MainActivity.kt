package dev.janssenbatista.shoppinglist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import dev.janssenbatista.shoppinglist.ui.screens.splash.SplashScreen
import dev.janssenbatista.shoppinglist.ui.theme.ShoppingListTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShoppingListTheme {
                Navigator(SplashScreen) {
                    SlideTransition(navigator = it)
                }
            }
        }
    }
}
