package pl.vemu.zsme.ui.more

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.net.toUri
import pl.vemu.zsme.R
import pl.vemu.zsme.launchCustomTabs


enum class ContactItem(
    @DrawableRes val icon: Int?,
    @StringRes val headerText: Int,
    @StringRes val text: Int,
    @StringRes private val action: Int,
) {
    /*TODO dynamic, icon from MaterialIcons*/
    NAME(null, R.string.school_name, R.string.school_name_text, 0),
    ADDRESS(
        R.drawable.ic_map,
        R.string.school_address,
        R.string.school_address_text,
        R.string.school_address_action
    ),
    OPEN(null, R.string.open_hours, R.string.open_hours_text, 0),
    PHONE(
        R.drawable.ic_phone,
        R.string.school_phone,
        R.string.school_phone_text,
        R.string.school_phone_text
    ),
    FAX(
        R.drawable.ic_phone,
        R.string.school_phone_fax,
        R.string.school_phone_fax_text,
        R.string.school_phone_fax_text
    ),
    SECRETARY(
        R.drawable.ic_mail,
        R.string.secretariat_email,
        R.string.secretariat_email_text,
        R.string.secretariat_email_text
    ),
    RECRUITMENT(
        R.drawable.ic_mail,
        R.string.recruitment_email,
        R.string.recruitment_email_text,
        R.string.recruitment_email_text
    ),
    ACCOUNTANCY(
        R.drawable.ic_mail,
        R.string.accountancy_email,
        R.string.accountancy_email_text,
        R.string.accountancy_email_text
    ),
    HEADMASTER(
        R.drawable.ic_mail,
        R.string.school_principal,
        R.string.school_principal_text,
        R.string.school_principal_action
    ),
    VICE_HEADMASTER_1(
        R.drawable.ic_mail,
        R.string.school_vice_principal,
        R.string.school_vice1_text,
        R.string.school_vice1_action
    ),
    VICE_HEADMASTER_2(
        R.drawable.ic_mail,
        R.string.school_vice_principal,
        R.string.school_vice2_text,
        R.string.school_vice2_action
    ),
    SOURCE_CODE(
        R.drawable.ic_github,
        R.string.source_code,
        R.string.source_code_text,
        R.string.github_url
    );

    fun onClick(context: Context) {
        val actionString = context.getString(action)
        try {
            when (icon) {
                R.drawable.ic_phone -> context.startActivity(
                    Intent(
                        Intent.ACTION_DIAL,
                        "tel:$actionString".toUri()
                    )
                )

                R.drawable.ic_mail -> context.startActivity(
                    Intent(
                        Intent.ACTION_SENDTO,
                        "mailto:$actionString".toUri()
                    )
                )

                else -> context.launchCustomTabs(actionString)
            }
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, R.string.no_app, Toast.LENGTH_LONG).show()
        }
    }
}
