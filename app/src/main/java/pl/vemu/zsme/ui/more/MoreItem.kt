package pl.vemu.zsme.ui.more

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ContactMail
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.ImportContacts
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.remoteConfig
import com.ramcosta.composedestinations.generated.destinations.ContactDestination
import com.ramcosta.composedestinations.generated.destinations.SettingsDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import pl.vemu.zsme.R
import pl.vemu.zsme.launchCustomTabs
import pl.vemu.zsme.util.baseUrl
import pl.vemu.zsme.util.journalUrl

enum class MoreItem(
    val icon: ImageVector,
    @StringRes val text: Int,
    val onClick: (context: Context, navController: DestinationsNavigator) -> Unit,
) {
    SETTINGS(
        Icons.Rounded.Settings,
        R.string.settings,
        { _, navController ->
            navController.navigate(SettingsDestination)
        }
    ),
    HOME_PAGE(Icons.Rounded.Home, R.string.home_page, { context, _ ->
        context.launchCustomTabs(Firebase.remoteConfig.baseUrl)
    }
    ),
    JOURNAL(
        Icons.Rounded.ImportContacts, R.string.e_dziennik, { context, _ ->
            context.launchCustomTabs(Firebase.remoteConfig.journalUrl)
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
