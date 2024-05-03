package dev.janssenbatista.shoppinglist.data.enums

import android.content.Context
import androidx.compose.ui.graphics.Color
import dev.janssenbatista.shoppinglist.R

enum class Colors(val id: Int, val color: Color, private val colorDescription: Int) {
    RED(1, Color(0xFFf44336), R.string.red),
    PINK(2, Color(0xFFe91e63), R.string.pink),
    PURPLE(3, Color(0xFF9c27b0), R.string.purple),
    DEEP_PURPLE(4, Color(0xFF673ab7), R.string.deep_purple),
    INDIGO(5, Color(0xFF3f51b5), R.string.indigo),
    BLUE(6, Color(0xFF2196f3), R.string.blue),
    LIGHT_BLUE(7, Color(0xFF03a9f4), R.string.light_blue),
    CYAN(8, Color(0xFF00bcd4), R.string.cyan),
    TEAL(9, Color(0xFF009688), R.string.teal),
    GREEN(10, Color(0xFF4caf50), R.string.green),
    LIGHT_GREEN(11, Color(0xFF8bc34a), R.string.light_green),
    LIME(12, Color(0xFFcddc39), R.string.lime),
    YELLOW(13, Color(0xFFffeb3b), R.string.yellow),
    ORANGE(14, Color(0xFFff9800), R.string.orange),
    DEEP_ORANGE(15, Color(0xFFff5722), R.string.deep_orange),
    BROWN(16, Color(0xFF795548), R.string.brown);

    fun getDescription(context: Context): String {
        return context.getString(colorDescription)
    }

    companion object {
        fun getColorById(id: Int): Colors {
            return Colors.entries.find { it.id == id }
                ?: throw IllegalArgumentException("Color not found")
        }
    }
}