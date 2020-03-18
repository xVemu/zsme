package pl.vemu.zsme.timetableFragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;
import java.util.Map;

import pl.vemu.zsme.databinding.FragmentTimetableBinding;

public class TimetableFragment extends Fragment implements SetMaps {

    private FragmentTimetableBinding binding;

    public TimetableFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTimetableBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        new DownloadTimetable(this).execute();
    }

    @Override
    public void makePageAdapter(List<Map<String, String>> maps) {
        binding.viewPager.setAdapter(new TimetablePageAdapter(this, maps));
        String[] names = {"Oddziały", "Nauczyciele", "Sale"};
        new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> tab.setText(names[position])).attach();
    }
}
