package pl.vemu.zsme.modifiers

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput

fun Modifier.noRippleClickable(onDoubleClick: (() -> Unit)? = null, onClick: () -> Unit) =
    this.pointerInput(Unit) {
        detectTapGestures(
            onDoubleTap = onDoubleClick?.let { { it() } },
            onTap = { onClick() },
        )
    }
