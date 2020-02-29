package pl.vemu.zsme.timetableFragment;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.apache.commons.collections4.map.LinkedMap;

import pl.vemu.zsme.databinding.FragmentTimetablePageBinding;

public class TimetableFragmentPage extends Fragment {

    private LinkedMap<String, String> map = new LinkedMap<>();
    private FragmentTimetablePageBinding binding;

    public TimetableFragmentPage() { }
    public TimetableFragmentPage(LinkedMap<String, String> map) {
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
        LinearLayoutManager linearManager = new LinearLayoutManager(getContext());
        RecyclerView recyclerView = (RecyclerView) binding.getRoot();
        recyclerView.setLayoutManager(linearManager);

        TimetableAdapter adapter = new TimetableAdapter(map);
        recyclerView.setAdapter(adapter);
    }
}
