package pl.vemu.zsme.ui.more

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.LocalContentColor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BrightnessMedium
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import de.schnettler.datastore.compose.model.Preference.PreferenceItem
import de.schnettler.datastore.compose.model.PreferenceIcon
import de.schnettler.datastore.compose.ui.PreferenceScreen
import de.schnettler.datastore.manager.DataStoreManager
import de.schnettler.datastore.manager.PreferenceRequest
import pl.vemu.zsme.R
import pl.vemu.zsme.ui.components.simpleSmallAppBar
import pl.vemu.zsme.ui.dataStore

@OptIn(
    ExperimentalComposeUiApi::class, ExperimentalMaterialApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun Settings(navController: NavController) {
    val context = LocalContext.current
    val dataStoreManager = DataStoreManager(context.dataStore)
    Scaffold(
        topBar = simpleSmallAppBar(
            title = R.string.settings,
            navController = navController
        )
    ) { paddding ->
        CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurface) {
            PreferenceScreen(
                modifier = Modifier.padding(paddding),
                items = listOf(
                    theme(),
                    language(),
                    notification(context)
                ),
                dataStoreManager = dataStoreManager
            )
        }
    }
}


@Preview(showBackground = true, backgroundColor = 0xFFFFFF)
@Composable
fun SettingsPreview() {
    Settings(rememberNavController())
}

@Composable
private fun theme() =
    PreferenceItem.ListPreference(
        request = PreferenceRequest(
            key = stringPreferencesKey("theme"),
            defaultValue = "system"
        ),
        title = stringResource(R.string.theme),
        summary = stringResource(R.string.system_default),
        singleLineTitle = true,
        icon = {
            PreferenceIcon(
                icon = Icons.Rounded.BrightnessMedium,
                contentDescription = stringResource(R.string.theme),
            )
        },
        entries = mapOf(
            "system" to stringResource(R.string.system_default),
            "false" to stringResource(R.string.light),
            "true" to stringResource(R.string.dark)
        )
    )

@Composable
private fun language() =
    PreferenceItem.ListPreference(
        request = PreferenceRequest(
            key = stringPreferencesKey("language"),
            defaultValue = "system"
        ),
        title = stringResource(R.string.language),
        summary = stringResource(R.string.system_default),
        singleLineTitle = true,
        icon = {
            PreferenceIcon(
                icon = Icons.Rounded.Language,
                contentDescription = stringResource(R.string.language),
            )
        },
        entries = mapOf(
            "system" to stringResource(R.string.system_default),
            "pl" to stringResource(R.string.polish),
            "en" to stringResource(R.string.english)
        )
    )

@Composable
private fun notification(context: Context) =
    PreferenceItem.TextPreference(
        title = stringResource(R.string.notification),
        singleLineTitle = false,
        summary = stringResource(R.string.notification_summary),
        icon = {
            PreferenceIcon(
                icon = Icons.Rounded.Notifications,
                contentDescription = stringResource(R.string.notification),
            )
        }
    ) {
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