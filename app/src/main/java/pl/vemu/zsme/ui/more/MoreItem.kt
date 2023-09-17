package pl.vemu.zsme.ui.more

import android.content.Context
import android.content.Intent
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ContactMail
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.ImportContacts
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.ramcosta.composedestinations.navigation.navigate
import pl.vemu.zsme.DEFAULT_URL
import pl.vemu.zsme.R
import pl.vemu.zsme.ui.destinations.ContactDestination
import pl.vemu.zsme.ui.destinations.SettingsDestination

enum class MoreItem(
    val icon: ImageVector,
    @StringRes val text: Int,
    val onClick: (context: Context, navController: NavController) -> Unit
) {
    SETTINGS(
        Icons.Rounded.Settings,
        R.string.settings,
        { _, navController ->
            navController.navigate(SettingsDestination)
        }
    ),
    HOME_PAGE(Icons.Rounded.Home, R.string.home_page, { context, _ ->
        context.startActivity(
            Intent.createChooser(
                Intent(
                    Intent.ACTION_VIEW,
                    DEFAULT_URL.toUri()
                ), context.getString(R.string.open_in)
            )
        )
    }
    ),
    JOURNAL(
        Icons.Rounded.ImportContacts, R.string.e_dziennik, { context, _ ->
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    "https://uonetplus.umt.tarnow.pl/tarnow".toUri()
                )
            )
        }
    ),
    CONTACT(
        Icons.Rounded.ContactMail,
        R.string.contact,
        { _, navController ->
            navController.navigate(ContactDestination)
        }
    );
}
