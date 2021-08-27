package pl.vemu.zsme.ui

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController

/*private val LightColors = lightColors(
    primary = Color(0xFF2B65B4),
    primaryVariant = Color(0xFF6eb3ed),
    secondary = Color(0xFFb47b2b),
    secondaryVariant = Color(0xFFdac58b)
)*/

private val LightColors = lightColors(
    primary = Color.White,
    primaryVariant = Color.White,
    secondary = Color(0xFF2B65B4),
    secondaryVariant = Color(0xFF6eb3ed),
    onPrimary = Color.Black,
    onSecondary = Color.White
)

private val DarkColors = darkColors( //TODO
    primary = Color(0xFF2B65B4),
    primaryVariant = Color(0xFF6eb3ed),
    secondary = Color(0xFFb47b2b),
    secondaryVariant = Color(0xFFdac58b)
)

@Composable
fun MainTheme(darkTheme: Boolean, content: @Composable () -> Unit) {
    val systemUiController = rememberSystemUiController()
    /*SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color.Transparent,
            darkIcons = !darkTheme
        )
    }*/
    MaterialTheme(
        colors = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}
