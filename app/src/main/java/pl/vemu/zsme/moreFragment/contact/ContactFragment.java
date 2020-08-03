package pl.vemu.zsme.moreFragment.contact;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.NotNull;

import pl.vemu.zsme.R;
import pl.vemu.zsme.databinding.FragmentContactBinding;
import pl.vemu.zsme.databinding.ItemContactBinding;
import pl.vemu.zsme.moreFragment.MoreAdapter;

public class ContactFragment extends Fragment {

    private FragmentContactBinding binding;

    public ContactFragment() {
    }


    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentContactBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding.getRoot().setAdapter(new MoreAdapter<ContactItem, ItemContactBinding>(R.layout.item_contact, ContactItem.class));
        binding.getRoot().setHasFixedSize(true);
    }
}
