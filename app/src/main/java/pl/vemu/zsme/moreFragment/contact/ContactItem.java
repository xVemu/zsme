package pl.vemu.zsme.moreFragment.contact;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pl.vemu.zsme.R;

@AllArgsConstructor
public enum ContactItem implements View.OnClickListener {
    NAME(0, R.string.school_name, R.string.school_name_text, 0),
    ADRESS(R.drawable.ic_map, R.string.school_address, R.string.school_address_text, R.string.school_address_action),
    EMAIL(R.drawable.ic_mail, R.string.school_email, R.string.school_email_text, R.string.school_email_text),
    PHONE(R.drawable.ic_phone, R.string.school_phone, R.string.school_phone_text, R.string.school_phone_text),
    FAX(R.drawable.ic_phone, R.string.school_phone_fax, R.string.school_phone_fax_text, R.string.school_phone_fax_text),
    HEADMASTER(R.drawable.ic_mail, R.string.school_principal, R.string.school_principal_text, R.string.school_principal_action),
    VICE_HEADMASTER_1(R.drawable.ic_mail, R.string.school_vice_principal, R.string.school_vice1_text, R.string.school_vice1_text),
    VICE_HEADMASTER_2(R.drawable.ic_mail, R.string.school_vice_principal, R.string.school_vice2_text, R.string.school_vice2_text);

    @Getter
    private final int icon, headerText, text, action;

    @Override
    public void onClick(View view) {
        Context context = view.getContext();
        String actionString = context.getString(action);
        Intent intent;
        switch (icon) {
            case R.drawable.ic_phone:
                intent = openPhone(actionString);
                break;
            case R.drawable.ic_mail:
                intent = openEmail(actionString);
                break;
            case R.drawable.ic_map:
                intent = openLocation(actionString);
                break;
            default:
                return;
        }
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }

    private Intent openPhone(String number) {
        Uri intentUri = Uri.parse("tel:" + number);
        return new Intent(Intent.ACTION_DIAL, intentUri);
    }

    private Intent openLocation(String location) {
        Uri intentUri = Uri.parse(location);
        return new Intent(Intent.ACTION_VIEW, intentUri);
    }

    private Intent openEmail(String email) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", email, null));
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        return emailIntent;
    }
}
