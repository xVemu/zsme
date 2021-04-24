package pl.vemu.zsme.ui

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import com.yariksoffice.lingver.Lingver
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val theme = preferences.getString("theme", "-1")?.toInt()
        theme?.let { AppCompatDelegate.setDefaultNightMode(it) }
        Lingver.init(this)
    }
}