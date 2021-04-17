package pl.vemu.zsme.moreFragment.more

import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.navigation.Navigation
import pl.vemu.zsme.R

enum class MoreItem(val icon: Int, val text: Int, val onClick: View.OnClickListener) {
    SETTINGS(R.drawable.ic_settings, R.string.settings, Navigation.createNavigateOnClickListener(MoreFragmentDirections.actionMoreFragmentToSettingsFragment())),
    HOME_PAGE(R.drawable.ic_home_page, R.string.home_page,
            View.OnClickListener { view: View -> view.context.startActivity(Intent.createChooser(Intent(Intent.ACTION_VIEW, Uri.parse("https://zsme.tarnow.pl")), view.context.getString(R.string.open_in))) }),
    E_DZIENNIK(R.drawable.ic_e_dziennik, R.string.e_dziennik,
            View.OnClickListener { view: View -> view.context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://uonetplus.umt.tarnow.pl/tarnow"))) }),
    CONTACT(R.drawable.ic_contact, R.string.contact, Navigation.createNavigateOnClickListener(MoreFragmentDirections.actionMoreFragmentToContactFragment()));
}