package pl.vemu.zsme.moreFragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

import pl.vemu.zsme.R;
import pl.vemu.zsme.databinding.FragmentContactBinding;

public class ContactFragment extends Fragment implements View.OnClickListener {

    private FragmentContactBinding binding;
    private Data data = Data.INSTANCE;

    public ContactFragment() {
    }


    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentContactBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        List<View> buttons = Arrays.asList(binding.navigateButton, binding.schoolEmailButton, binding.schoolHeadmasterButton,
                binding.schoolTelephoneButton, binding.schoolTelephoneFaxButton, binding.schoolVice1Button, binding.schoolVice2Button);
        for (View button : buttons) {
            button.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.navigateButton:
                Intent intent = openLocation(data.location);
                if (intent != null) startActivity(intent);
                break;
            case R.id.schoolTelephoneButton:
                startActivity(openPhone(data.phone));
                break;
            case R.id.schoolTelephoneFaxButton:
                startActivity(openPhone(data.fax));
                break;
            case R.id.schoolEmailButton:
                startActivity(openEmail(data.email));
                break;
            case R.id.schoolHeadmasterButton:
                startActivity(openEmail(data.emailHeadmaster));
                break;
            case R.id.schoolVice1Button:
                startActivity(openEmail(data.emailVice1));
                break;
            case R.id.schoolVice2Button:
                startActivity(openEmail(data.emailVice2));
                break;
            default:
                break;
        }
    }

    private Intent openPhone(String number) {
        Uri intentUri = Uri.parse("tel:" + number);
        return new Intent(Intent.ACTION_DIAL, intentUri);
    }

    private Intent openLocation(String location) {
        Uri intentUri = Uri.parse(location);
        Intent intent = new Intent(Intent.ACTION_VIEW, intentUri);
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            return intent;
        }
        return null;
    }

    private Intent openEmail(String email) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", email, null));
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        return emailIntent;
    }

    private enum Data {
        INSTANCE;

        private String email = "sekretzsme@umt.tarnow.pl",
                phone = "(014) 621 61 84",
                fax = "(014) 621 61 83",
                emailHeadmaster = "dyrzsme@umt.tarnow.pl",
                emailVice1 = "m.piszczek@zsme.tarnow.pl",
                emailVice2 = "g.szerszen@zsme.tarnow.pl",
                location = "https://www.google.com/maps/place/Zesp%C3%B3%C5%82+Szk%C3%B3%C5%82+Mechaniczno+-+Elektrycznych/@50.0154782,20.9749706,18.58z/data=!4m5!3m4!1s0x0:0xc3a2ae398241c00!8m2!3d50.0154716!4d20.975434";
    }
}
