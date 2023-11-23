package pl.vemu.zsme.modifiers

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed

fun Modifier.noRippleClickable(onClick: () -> Unit) = composed {
    clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() },
        onClick = onClick
    )
}

@OptIn(ExperimentalFoundationApi::class)
fun Modifier.noRippleCombinedClickable(onDoubleClick: (() -> Unit)? = null, onClick: () -> Unit) =
    composed {
        combinedClickable(
            indication = null,
            interactionSource = remember { MutableInteractionSource() },
            onClick = onClick,
            onDoubleClick = onDoubleClick,
        )
    }
