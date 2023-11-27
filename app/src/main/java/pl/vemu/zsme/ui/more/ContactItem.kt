package pl.vemu.zsme.ui.more

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.AssistantDirection
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.Email
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.core.net.toUri
import pl.vemu.zsme.R
import pl.vemu.zsme.launchCustomTabs


enum class ContactItem(
    val icon: ImageVector?,
    @StringRes val headerText: Int,
    @StringRes val text: Int,
    @StringRes private val action: Int,
) {
    NAME(null, R.string.school_name, R.string.school_name_text, 0),
    ADDRESS(
        Icons.AutoMirrored.Rounded.AssistantDirection,
        R.string.school_address,
        R.string.school_address_text,
        R.string.school_address_action
    ),
    OPEN(null, R.string.open_hours, R.string.open_hours_text, 0),
    PHONE(
        Icons.Rounded.Call,
        R.string.school_phone,
        R.string.school_phone_text,
        R.string.school_phone_text
    ),
    FAX(
        Icons.Rounded.Call,
        R.string.school_phone_fax,
        R.string.school_phone_fax_text,
        R.string.school_phone_fax_text
    ),
    SECRETARY(
        Icons.Rounded.Email,
        R.string.secretariat_email,
        R.string.secretariat_email_text,
        R.string.secretariat_email_text
    ),
    RECRUITMENT(
        Icons.Rounded.Email,
        R.string.recruitment_email,
        R.string.recruitment_email_text,
        R.string.recruitment_email_text
    ),
    ACCOUNTANCY(
        Icons.Rounded.Email,
        R.string.accountancy_email,
        R.string.accountancy_email_text,
        R.string.accountancy_email_text
    ),
    HEADMASTER(
        Icons.Rounded.Email,
        R.string.school_principal,
        R.string.school_principal_text,
        R.string.school_principal_action
    ),
    VICE_HEADMASTER_1(
        Icons.Rounded.Email,
        R.string.school_vice_principal,
        R.string.school_vice1_text,
        R.string.school_vice1_action
    ),
    VICE_HEADMASTER_2(
        Icons.Rounded.Email,
        R.string.school_vice_principal,
        R.string.school_vice2_text,
        R.string.school_vice2_action
    ),
    SOURCE_CODE(
        Icons.Rounded.Github,
        R.string.source_code,
        R.string.source_code_text,
        R.string.github_url
    );

    fun onClick(context: Context) {
        val actionString = context.getString(action)
        try {
            when (icon) {
                Icons.Rounded.Call -> context.startActivity(
                    Intent(
                        Intent.ACTION_DIAL,
                        "tel:$actionString".toUri()
                    )
                )

                Icons.Rounded.Email -> context.startActivity(
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
