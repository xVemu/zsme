package pl.vemu.zsme

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ColorScheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.ln

fun Modifier.paddingStart(size: Dp = 0.dp) = this.padding(size, 0.dp, 0.dp, 0.dp)
fun Modifier.paddingTop(size: Dp = 0.dp) = this.padding(0.dp, size, 0.dp, 0.dp)
fun Modifier.paddingEnd(size: Dp = 0.dp) = this.padding(0.dp, 0.dp, size, 0.dp)
fun Modifier.paddingBottom(size: Dp = 0.dp) = this.padding(0.dp, 0.dp, 0.dp, size)

fun ColorScheme.surfaceColorWithElevation(
    elevation: Dp,
) = primary.copy(alpha = ((4.5f * ln(elevation.value + 1)) + 2f) / 100f).compositeOver(surface)