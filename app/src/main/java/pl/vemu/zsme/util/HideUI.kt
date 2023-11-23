package pl.vemu.zsme.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.Window
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.DialogWindowProvider
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

@Composable
fun rememberInsetsController(): WindowInsetsControllerCompat? {
    val window = findWindow()
    val view = LocalView.current
    return remember(view, window) { window?.let { WindowCompat.getInsetsController(it, view) } }
}

@Composable
private fun findWindow(): Window? =
    (LocalView.current.parent as? DialogWindowProvider)?.window
        ?: LocalView.current.context.findWindow()

private tailrec fun Context.findWindow(): Window? =
    when (this) {
        is Activity -> window
        is ContextWrapper -> baseContext.findWindow()
        else -> null
    }

fun WindowInsetsControllerCompat.hideUi() {
    systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    hide(WindowInsetsCompat.Type.systemBars())
}

fun WindowInsetsControllerCompat.showUi() {
    show(WindowInsetsCompat.Type.systemBars())
    systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
}

fun WindowInsetsControllerCompat.changeUiVisibility(visible: Boolean) {
    if (visible) showUi()
    else hideUi()
}
