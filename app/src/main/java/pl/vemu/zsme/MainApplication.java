package pl.vemu.zsme;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import com.yariksoffice.lingver.Lingver;

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int theme = Integer.parseInt(preferences.getString("theme", "-1"));
        AppCompatDelegate.setDefaultNightMode(theme);
        Lingver.init(this);
    }
}
