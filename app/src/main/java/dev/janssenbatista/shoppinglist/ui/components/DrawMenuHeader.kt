package dev.janssenbatista.shoppinglist.ui.components


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import dev.janssenbatista.shoppinglist.R

@Composable
fun DrawMenuHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .padding(horizontal = 24.dp).padding(top = 32.dp, bottom = 8.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Image(painterResource(id = R.drawable.wishlist_icon), contentDescription = "app logo")
    }
}