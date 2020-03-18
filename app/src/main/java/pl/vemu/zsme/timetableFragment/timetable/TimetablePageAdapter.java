package pl.vemu.zsme.timetableFragment.timetable;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;
import java.util.Map;

public class TimetablePageAdapter extends FragmentStateAdapter {

    private final List<Map<String, String>> maps;

    TimetablePageAdapter(@NonNull Fragment fragment, List<Map<String, String>> maps) {
        super(fragment);
        this.maps = maps;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return new TimetableFragmentPage(maps.get(position));
    }

    @Override
    public int getItemCount() {
        return maps.size();
    }
}
