package pl.vemu.zsme.moreFragment;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.yariksoffice.lingver.Lingver;

import pl.vemu.zsme.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    public SettingsFragment() {
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey);
        ListPreference theme = findPreference("theme");
        ListPreference language = findPreference("language");
        Preference button = findPreference("notification");
        if (theme != null) {
            theme.setOnPreferenceChangeListener((preference, newValue) -> {
                AppCompatDelegate.setDefaultNightMode(Integer.parseInt((String) newValue));
                return true;
            });
        }
        if (button != null) {
            button.setOnPreferenceClickListener(preference -> {
                Intent intent = new Intent().setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    intent.putExtra("android.provider.extra.APP_PACKAGE", requireContext().getPackageName());
                } else {
                    intent.putExtra("app_package", requireContext().getPackageName());
                    intent.putExtra("app_uid", requireContext().getApplicationInfo().uid);
                }
                startActivity(intent);
                return true;
            });
        }
        if (language != null) {
            language.setOnPreferenceChangeListener((preference, newValue) -> {
                if (newValue == "system") {
                    Lingver.getInstance().setFollowSystemLocale(requireContext());
                } else {
                    Lingver.getInstance().setLocale(requireContext(), (String) newValue);
                }
                requireActivity().recreate();
                return true;
            });
        }
    }

}
