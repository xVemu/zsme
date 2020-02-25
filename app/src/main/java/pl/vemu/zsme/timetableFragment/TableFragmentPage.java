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

import java.util.ArrayList;
import java.util.List;

import pl.vemu.zsme.R;

public class TableFragmentPage extends Fragment {

    private TableAdapter tableAdapter;
    private List<Lesson> lessons = new ArrayList<>();

    public TableFragmentPage() { }
    public TableFragmentPage(List<Lesson> lessons) {
        this.lessons = lessons;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_table_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recView = view.findViewById(R.id.recViewTable);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recView.setLayoutManager(manager);

        tableAdapter = new TableAdapter(lessons);
        recView.setAdapter(tableAdapter);
    }
}
