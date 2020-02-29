package pl.vemu.zsme.timetableFragment;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import pl.vemu.zsme.databinding.FragmentTablePageBinding;

public class TableFragmentPage extends Fragment {

    private List<Lesson> lessons = new ArrayList<>();
    private String day;
    private FragmentTablePageBinding binding;

    public TableFragmentPage() { }
    public TableFragmentPage(List<Lesson> lessons, String day) {
        this.lessons = lessons;
        this.day = day;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTablePageBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        binding.recyclerView.setLayoutManager(manager);

        binding.day.setText(day);

        TableAdapter tableAdapter = new TableAdapter(lessons);
        binding.recyclerView.setAdapter(tableAdapter);
    }
}
