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
import androidx.navigation.Navigation;

import pl.vemu.zsme.databinding.FragmentMoreBinding;


public class MoreFragment extends Fragment {

    private FragmentMoreBinding binding;

    public MoreFragment() {
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMoreBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding.settings.setOnClickListener(Navigation.createNavigateOnClickListener(MoreFragmentDirections.actionMoreFragmentToSettingsFragment()));
        binding.page.setOnClickListener(v -> getActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://zsme.tarnow.pl"))));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
