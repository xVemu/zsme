package pl.vemu.zsme.timetableFragment.timetable;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.apache.commons.collections4.map.LinkedMap;

import java.util.Map;

import pl.vemu.zsme.databinding.FragmentTimetablePageBinding;

public class TimetablePageFragment extends Fragment {

    private Map<String, String> map = new LinkedMap<>();
    private FragmentTimetablePageBinding binding;

    public TimetablePageFragment() {
    }

    public TimetablePageFragment(Map<String, String> map) {
        this.map = map;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTimetablePageBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding.getRoot().setAdapter(new TimetableAdapter(map));
    }
}
