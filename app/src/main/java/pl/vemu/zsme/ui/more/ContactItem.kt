package pl.vemu.zsme.ui.more

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import pl.vemu.zsme.R

enum class ContactItem(
    @DrawableRes val icon: Int?,
    @StringRes val headerText: Int,
    @StringRes val text: Int,
    @StringRes private val action: Int
) {
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
    SECRETARIAT(
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
        val intent = when (icon) {
            R.drawable.ic_phone -> Intent(Intent.ACTION_DIAL, Uri.parse("tel:$actionString"))
            R.drawable.ic_mail -> Intent(Intent.ACTION_SENDTO)
                .run { putExtra(Intent.EXTRA_EMAIL, arrayOf(actionString)) }
            else -> Intent(Intent.ACTION_VIEW, Uri.parse(actionString))
        }
        if (intent.resolveActivity(context.packageManager) != null) context.startActivity(intent)
        else Toast.makeText(context, R.string.no_app, Toast.LENGTH_LONG).show()
    }
}