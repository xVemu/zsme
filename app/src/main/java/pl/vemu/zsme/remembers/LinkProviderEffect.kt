package pl.vemu.zsme.remembers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay

var link: String? = null

@Composable
fun LinkProviderEffect(url: String) {
    // DisposableEffect is not suspended. Idk if it should change coroutine context.
    LaunchedEffect(url) {
        // Needs delay because onDispose is called after settings an url.
        delay(1000)
        link = url
    }

    DisposableEffect(url) {
        onDispose {
            link = null
        }
    }
}
