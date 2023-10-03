package pl.vemu.zsme.ui.more

import android.content.Intent
import android.os.Build
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BrightnessMedium
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.alorma.compose.settings.ui.SettingsList
import com.alorma.compose.settings.ui.SettingsMenuLink
import com.ramcosta.composedestinations.annotation.Destination
import pl.vemu.zsme.R
import pl.vemu.zsme.remembers.Prefs
import pl.vemu.zsme.remembers.rememberStringPreference
import pl.vemu.zsme.ui.components.SimpleMediumAppBar
import pl.vemu.zsme.ui.components.SlideTransition

@OptIn(ExperimentalMaterial3Api::class)
@MoreNavGraph
@Destination(route = "more/settings", style = SlideTransition::class)
@Composable
fun Settings(navController: NavController) {
    Scaffold(
        topBar = {
            SimpleMediumAppBar(
                title = R.string.settings,
                navController = navController
            )
        }) { padding ->
        Column(Modifier.padding(padding)) {
            Theme()
            Language()
            Notification()
        }
    }
}


@Preview(showBackground = true, backgroundColor = 0xFFFFFF)
@Composable
fun SettingsPreview() {
    Settings(rememberNavController())
}

@Composable
private fun Theme() {
    var theme by rememberStringPreference(Prefs.THEME)
    val items = mapOf(
        "system" to stringResource(R.string.system_default),
        "false" to stringResource(R.string.light),
        "true" to stringResource(R.string.dark)
    )

    SettingsList(
        title = { Text(stringResource(R.string.theme)) },
        icon = {
            Icon(
                Icons.Rounded.BrightnessMedium,
                contentDescription = stringResource(R.string.theme)
            )
        },
        subtitle = { Text(items[theme]!!) },
        items = items.values.toList(),
    ) { index, _ ->
        theme = items.keys.elementAt(index)
    }
}

@Composable
private fun Language() {
    var lang by rememberStringPreference(Prefs.LANGUAGE)
    val items = mapOf(
        "system" to stringResource(R.string.system_default),
        "pl" to stringResource(R.string.polish),
        "en" to stringResource(R.string.english)
    )

    SettingsList(
        title = { Text(stringResource(R.string.language)) },
        icon = {
            Icon(
                Icons.Rounded.Language,
                contentDescription = stringResource(R.string.language)
            )
        },
        subtitle = { Text(items[lang]!!) },
        items = items.values.toList(),
    ) { index, _ ->
        lang = items.keys.elementAt(index)
    }
}

@Composable
private fun Notification() {
    val context = LocalContext.current

    SettingsMenuLink(
        title = { Text(stringResource(R.string.notification)) },
        subtitle = {
            Text(
                stringResource(R.string.notification_summary)
            )
        },
        icon = {
            Icon(
                Icons.Rounded.Notifications,
                contentDescription = stringResource(R.string.notification)
            )
        }) {
        val intent = Intent().apply {
            action = "android.settings.APP_NOTIFICATION_SETTINGS"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                putExtra("android.provider.extra.APP_PACKAGE", context.packageName)
            else {
                putExtra("app_package", context.packageName)
                putExtra("app_uid", context.applicationInfo.uid)
            }
        }
        context.startActivity(intent)
    }
}
