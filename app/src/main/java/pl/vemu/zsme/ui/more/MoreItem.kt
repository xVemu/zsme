package pl.vemu.zsme.ui.more

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ContactMail
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.ImportContacts
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import pl.vemu.zsme.R

enum class MoreItem(
    val icon: ImageVector,
    @StringRes val text: Int,
    val onClick: (context: Context, navController: NavController) -> Unit
) {
    SETTINGS(
        Icons.Rounded.Settings,
        R.string.settings,
        { _, navController ->
            navController.navigate("settings")
        }
    ),
    HOME_PAGE(Icons.Rounded.Home, R.string.home_page, { context, _ ->
        context.startActivity(
            Intent.createChooser(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://zsme.tarnow.pl")
                ), context.getString(R.string.open_in)
            )
        )
    }
    ),
    E_DZIENNIK(
        Icons.Rounded.ImportContacts, R.string.e_dziennik, { context, _ ->
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://uonetplus.umt.tarnow.pl/tarnow")
                )
            )
        }
    ),
    CONTACT(
        Icons.Rounded.ContactMail,
        R.string.contact,
        { _, navController ->
            navController.navigate("contact")
        }
    );
}