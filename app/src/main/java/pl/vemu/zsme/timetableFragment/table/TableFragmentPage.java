package pl.vemu.zsme.timetableFragment.table;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import pl.vemu.zsme.databinding.FragmentTablePageBinding;

public class TableFragmentPage extends Fragment {

    private List<Lesson> lessons = new ArrayList<>();
    private FragmentTablePageBinding binding;

    public TableFragmentPage() {
    }

    public TableFragmentPage(List<Lesson> lessons) {
        this.lessons = lessons;
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
        RecyclerView recyclerView = (RecyclerView) binding.getRoot();
        recyclerView.setLayoutManager(manager);

        RecyclerView.Adapter tableAdapter = new TableAdapter(lessons);
        recyclerView.setAdapter(tableAdapter);
    }
}
