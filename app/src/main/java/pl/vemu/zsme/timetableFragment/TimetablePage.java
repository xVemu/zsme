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

import lombok.RequiredArgsConstructor;
import pl.vemu.zsme.R;

@RequiredArgsConstructor
public class TimetablePage extends Fragment {

    private final LinkedMap<String, String> map;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_timetable_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recView = view.findViewById(R.id.recViewTimetable);
        LinearLayoutManager linearManager = new LinearLayoutManager(getContext());
        recView.setLayoutManager(linearManager);

        TimetableAdapter adapter = new TimetableAdapter(map);
        recView.setAdapter(adapter);
    }
}
