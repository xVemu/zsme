package pl.vemu.zsme.remembers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt

@Composable
fun rememberFloatingTopBar(toolbarHeight: Dp): FloatingTopBarState {
    val toolbarHeightPx = with(LocalDensity.current) { toolbarHeight.roundToPx().toFloat() }

    val toolbarOffsetHeightPx = remember { mutableFloatStateOf(0f) }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                val newOffset = toolbarOffsetHeightPx.floatValue + delta
                toolbarOffsetHeightPx.floatValue = newOffset.coerceIn(-toolbarHeightPx, 0f)
                return Offset.Zero
            }
        }
    }

    return FloatingTopBarState(nestedScrollConnection, toolbarOffsetHeightPx.floatValue)
}

class FloatingTopBarState(
    val nestedScrollConnection: NestedScrollConnection,
    private val toolbarOffsetHeightPx: Float,
) {
    val offset: Density.() -> IntOffset =
        { IntOffset(x = 0, y = toolbarOffsetHeightPx.roundToInt()) }
}
