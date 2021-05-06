package pl.vemu.zsme.ui.more

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.yariksoffice.lingver.Lingver
import pl.vemu.zsme.R

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)
        val theme: ListPreference? = findPreference("theme")
        val language: ListPreference? = findPreference("language")
        val button: Preference? = findPreference("notification")
        theme?.apply {
            setOnPreferenceChangeListener { _, newValue ->
                AppCompatDelegate.setDefaultNightMode(newValue.toString().toInt())
                true
            }
        }
        button?.apply {
            setOnPreferenceClickListener {
                val intent = Intent().apply {
                    action = "android.settings.APP_NOTIFICATION_SETTINGS"
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                        putExtra("android.provider.extra.APP_PACKAGE", context.packageName)
                    else {
                        putExtra("app_package", context.packageName)
                        putExtra("app_uid", context.applicationInfo.uid)
                    }
                }
                startActivity(intent)
                true
            }
        }
        language?.apply {
            setOnPreferenceChangeListener { _, newValue ->
                Lingver.getInstance().apply {
                    if (newValue === "system") setFollowSystemLocale(context)
                    else setLocale(context, newValue.toString())
                }
                activity?.recreate()
                true
            }
        }
    }
}