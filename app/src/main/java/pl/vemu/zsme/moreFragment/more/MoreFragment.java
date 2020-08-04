package pl.vemu.zsme.moreFragment.more;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import pl.vemu.zsme.BaseAdapter;
import pl.vemu.zsme.R;
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
        binding.getRoot().setAdapter(new BaseAdapter(R.layout.item_more, MoreItem.values()));
        binding.getRoot().setHasFixedSize(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
