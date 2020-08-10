package pl.vemu.zsme.moreFragment.more;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

import androidx.navigation.Navigation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pl.vemu.zsme.R;

@AllArgsConstructor
public enum MoreItem {
    SETTINGS(R.drawable.ic_settings, R.string.settings) {
        @Override
        public View.OnClickListener onClick(Context context) {
            return Navigation.createNavigateOnClickListener(MoreFragmentDirections.actionMoreFragmentToSettingsFragment());
        }
    },
    HOME_PAGE(R.drawable.ic_home_page, R.string.home_page) {
        @Override
        public View.OnClickListener onClick(Context context) {
            return (view -> context.startActivity(Intent.createChooser(new Intent(Intent.ACTION_VIEW, Uri.parse("https://zsme.tarnow.pl")), view.getContext().getString(R.string.open_in))));
        }
    },
    E_DZIENNIK(R.drawable.ic_e_dziennik, R.string.e_dziennik) {
        @Override
        public View.OnClickListener onClick(Context context) {
            return (view -> context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://uonetplus.umt.tarnow.pl/tarnow"))));
        }
    },
    CONTACT(R.drawable.ic_contact, R.string.contact) {
        @Override
        public View.OnClickListener onClick(Context context) {
            return Navigation.createNavigateOnClickListener(MoreFragmentDirections.actionMoreFragmentToContactFragment());
        }
    };

    @Getter
    private final int icon,
            text;

    public abstract View.OnClickListener onClick(Context context);
}
