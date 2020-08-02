package pl.vemu.zsme.timetableFragment.table;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import pl.vemu.zsme.databinding.FragmentTablePageBinding;

public class TablePageFragment extends Fragment {

    private List<Lesson> lessons = new ArrayList<>();
    private FragmentTablePageBinding binding;

    public TablePageFragment() {
    }

    public TablePageFragment(List<Lesson> lessons) {
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
        binding.getRoot().setAdapter(new TableAdapter(lessons));
    }
}
