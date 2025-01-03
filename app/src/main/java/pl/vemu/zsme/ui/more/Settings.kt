package pl.vemu.zsme.ui.more

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BrightnessMedium
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alorma.compose.settings.storage.base.rememberIntSettingState
import com.alorma.compose.settings.ui.SettingsList
import com.alorma.compose.settings.ui.SettingsMenuLink
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.yariksoffice.lingver.Lingver
import pl.vemu.zsme.R
import pl.vemu.zsme.remembers.Prefs
import pl.vemu.zsme.remembers.rememberStringPreference
import pl.vemu.zsme.ui.components.SimpleMediumAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Destination<MoreNavGraph>
@Composable
fun Settings(navController: DestinationsNavigator) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        topBar = {
            SimpleMediumAppBar(
                title = R.string.settings,
                navController = navController,
                scrollBehavior = scrollBehavior,
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { padding ->
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(padding),
        ) {
            Theme()
            Language()
            Notification()
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFF)
@Composable
fun SettingsPreview() {
    Settings(EmptyDestinationsNavigator)
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
        modifier = Modifier.height(88.dp),
        title = { Text(stringResource(R.string.theme)) },
        icon = {
            Icon(
                Icons.Rounded.BrightnessMedium,
                contentDescription = stringResource(R.string.theme)
            )
        },
        items = items.values.toList(),
        state = rememberIntSettingState(defaultValue = items.keys.indexOf(theme))
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
    val context = LocalContext.current

    SettingsList(
        modifier = Modifier.height(88.dp),
        title = { Text(stringResource(R.string.language)) },
        icon = {
            Icon(
                Icons.Rounded.Language,
                contentDescription = stringResource(R.string.language)
            )
        },
        subtitle = { Text(items[lang]!!) },
        items = items.values.toList(),
        state = rememberIntSettingState(defaultValue = items.keys.indexOf(lang))
    ) { index, _ ->
        val item = items.keys.elementAt(index)
        lang = item
        Lingver.getInstance().apply {
            if (item == "system") setFollowSystemLocale(context)
            else setLocale(context, item)
        }
    }
}

@Composable
private fun Notification() {
    val context = LocalContext.current

    SettingsMenuLink(
        modifier = Modifier.height(88.dp),
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
            putExtra("android.provider.extra.APP_PACKAGE", context.packageName)
        }
        context.startActivity(intent)
    }
}
