package pl.vemu.zsme.timetableFragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import org.apache.commons.collections4.map.LinkedMap;

import java.util.List;

public class TimetablePageAdapter extends FragmentStateAdapter {

    private final List<LinkedMap<String, String>> maps;

    TimetablePageAdapter(@NonNull Fragment fragment, List<LinkedMap<String, String>> maps) {
        super(fragment);
        this.maps = maps;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return new TimetablePage(maps.get(position));
    }

    @Override
    public int getItemCount() {
        return maps.size();
    }
}
