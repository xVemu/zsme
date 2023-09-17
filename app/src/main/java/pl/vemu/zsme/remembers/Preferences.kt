package pl.vemu.zsme.remembers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import pl.vemu.zsme.ui.dataStore
import kotlin.reflect.KProperty

enum class Prefs constructor(val key: Preferences.Key<String>, val defaultValue: String) {
    THEME(stringPreferencesKey("theme"), "system"),
    LANGUAGE(stringPreferencesKey("language"), "system"),
}

@Composable
fun rememberStringPreference(
    prefs: Prefs,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    dataStore: DataStore<Preferences> = LocalContext.current.dataStore,
): PreferenceState<String> {
    return remember {
        PreferenceState(
            coroutineScope = coroutineScope,
            dataStore = dataStore,
            key = prefs.key,
            defaultValue = prefs.defaultValue,
        )
    }
}

class PreferenceState<T>(
    private val coroutineScope: CoroutineScope,
    private val dataStore: DataStore<Preferences>,
    private val key: Preferences.Key<T>,
    private val defaultValue: T,
) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T = value

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        this.value = value
    }

    private val flow = dataStore.data.map { it[key] ?: defaultValue }

    private var _value: T by mutableStateOf(runBlocking { flow.first() })

    init {
        coroutineScope.launch {
            flow.collect { _value = it }
        }
    }

    var value: T
        get() = _value
        set(value) {
//            _value = value TODO needed?
            coroutineScope.launch {
                dataStore.edit {
                    it[key] = value
                }
            }
        }
}
