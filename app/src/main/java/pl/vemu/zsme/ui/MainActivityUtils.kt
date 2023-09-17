package pl.vemu.zsme.ui

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DynamicFeed
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Subject
import androidx.compose.material.icons.rounded.DynamicFeed
import androidx.compose.material.icons.rounded.EventNote
import androidx.compose.material.icons.rounded.Subject
import androidx.compose.ui.graphics.vector.ImageVector
import com.ramcosta.composedestinations.spec.NavGraphSpec
import pl.vemu.zsme.R

enum class BottomNavItem(
    val destination: NavGraphSpec,
    val icon: ImageVector,
    val iconFilled: ImageVector,
    @StringRes val label: Int
) {
    POST(NavGraphs.post, Icons.Rounded.DynamicFeed, Icons.Filled.DynamicFeed, R.string.post),
    TIMETABLE(
        NavGraphs.timetable,
        Icons.Rounded.EventNote,
        Icons.Filled.EventNote,
        R.string.timetable
    ),
    MORE(NavGraphs.more, Icons.Rounded.Subject, Icons.Filled.Subject, R.string.more);
}
