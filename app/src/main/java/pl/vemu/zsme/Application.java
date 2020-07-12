package pl.vemu.zsme;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        int theme = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(this).getString("theme", "-1"));
        AppCompatDelegate.setDefaultNightMode(theme);
    }
}
