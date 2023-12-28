package pl.vemu.zsme.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.TabPosition
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.lerp
import kotlin.math.abs

// Comes from accompanist/pager-indicators
internal fun MeasureScope.measureTabIndicatorOffset(
    tabPositions: List<TabPosition>,
    currentPage: Int,
    fraction: Float,
    measurable: Measurable,
    constraints: Constraints,
    useFullWidth: Boolean = false,
): MeasureResult {
    val currentTab = tabPositions[currentPage]
    val previousTab = tabPositions.getOrNull(currentPage - 1)
    val nextTab = tabPositions.getOrNull(currentPage + 1)

    val width = lerp(
        currentTab.getWidth(useFullWidth),
        getTab(previousTab, currentTab, nextTab, fraction).getWidth(useFullWidth),
        abs(fraction),
    )

    val offset = lerp(
        currentTab.getOffset(useFullWidth),
        getTab(previousTab, currentTab, nextTab, fraction).getOffset(useFullWidth),
        abs(fraction),
    )

    val placeable = measurable.measure(
        Constraints(
            minWidth = width.roundToPx(),
            maxWidth = width.roundToPx(),
            minHeight = 0,
            maxHeight = constraints.maxHeight
        )
    )

    return layout(constraints.maxWidth, maxOf(placeable.height, constraints.minHeight)) {
        placeable.placeRelative(
            offset.roundToPx(),
            maxOf(constraints.minHeight - placeable.height, 0)
        )
    }
}

private fun getTab(
    previousTab: TabPosition?,
    currentTab: TabPosition,
    nextTab: TabPosition?,
    fraction: Float,
) = when {
    fraction > 0 && nextTab != null -> nextTab
    fraction < 0 && previousTab != null -> previousTab
    else -> currentTab
}

private fun TabPosition.getOffset(useFullWidth: Boolean = false) =
    if (useFullWidth) left else midOffset

private fun TabPosition.getWidth(useFullWidth: Boolean = false) =
    if (useFullWidth) width else contentWidth

private val TabPosition.midOffset: Dp
    get() = left + (width - contentWidth) / 2

@OptIn(ExperimentalFoundationApi::class)
internal fun Modifier.pagerTabIndicatorOffset(
    pagerState: PagerState,
    tabPositions: List<TabPosition>,
    useFullWidth: Boolean = false,
) = layout { measurable, constraints ->
    measureTabIndicatorOffset(
        tabPositions = tabPositions,
        currentPage = pagerState.currentPage,
        fraction = pagerState.currentPageOffsetFraction,
        measurable = measurable,
        constraints = constraints,
        useFullWidth = useFullWidth,
    )
}
