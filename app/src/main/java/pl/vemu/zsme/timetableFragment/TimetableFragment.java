package pl.vemu.zsme.timetableFragment;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.apache.commons.collections4.map.LinkedMap;

import java.util.List;

import pl.vemu.zsme.R;

public class TimetableFragment extends Fragment implements ISetMaps{

    private ViewPager2 viewPager;
    private TabLayout tabLayout;

    public TimetableFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_timetable, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        tabLayout = view.findViewById(R.id.tab_layout);
        viewPager = view.findViewById(R.id.pager);
        new DownloadTimetable(this).execute();
    }

    @Override
    public void makePageAdapter(List<LinkedMap<String, String>> maps) {
        viewPager.setAdapter(new TimetablePageAdapter(this, maps));
        String[] names = {"Oddziały", "Nauczyciele", "Sale"};
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> tab.setText(names[position])).attach();
    }
}
