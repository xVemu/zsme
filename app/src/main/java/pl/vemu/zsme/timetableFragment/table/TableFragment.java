package pl.vemu.zsme.timetableFragment.table;

import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;

import pl.vemu.zsme.databinding.FragmentTableBinding;
import pl.vemu.zsme.timetableFragment.TableFragmentArgs;

public class TableFragment extends Fragment implements TableDownload {

    private FragmentTableBinding binding;

    public TableFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTableBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        String url = TableFragmentArgs.fromBundle(getArguments()).getUrl();
        new DownloadTable(this).execute(url);
    }

    @Override
    public void makeAdapter(List<List<Lesson>> lessons) {
        binding.viewPager.setAdapter(new TablePageAdapter(this, lessons));
        String[] names = {"Poniedziałek", "Wtorek", "Środa", "Czwartek", "Piątek"};
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
            day = (day == 1) || (day == 7) ? 0 : day - 2;
            binding.viewPager.setCurrentItem(day);
        }
        new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> tab.setText(names[position])).attach();
    }
}
