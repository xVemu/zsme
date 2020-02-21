package pl.vemu.zsme.timetableFragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

class TablePageAdapter extends FragmentStateAdapter {

    private final List<List<Lesson>> lessons;

    public TablePageAdapter(@NonNull Fragment fragment, List<List<Lesson>> lessons) {
        super(fragment);
        this.lessons = lessons;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return new TableFragmentPage(lessons.get(position));
    }

    @Override
    public int getItemCount() {
        return lessons.size();
    }
}
