package pl.vemu.zsme.timetableFragment.table;

import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;

import pl.vemu.zsme.R;
import pl.vemu.zsme.databinding.FragmentTableBinding;
import pl.vemu.zsme.timetableFragment.TableTimetableAdapter;

public class TableFragment extends Fragment {

    private FragmentTableBinding binding;
    private TableFragmentVM viewmodel;

    public TableFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        String url = TableFragmentArgs.fromBundle(getArguments()).getUrl();
        binding = FragmentTableBinding.inflate(inflater, container, false);
        viewmodel = new ViewModelProvider(this, new TableFragmentVMFactory(url)).get(TableFragmentVM.class);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        TableTimetableAdapter adapter = new TableTimetableAdapter(R.layout.item_table, (List) viewmodel.getList().getValue());
        binding.viewPager.setAdapter(adapter);
        String[] names = {getString(R.string.monday), getString(R.string.tuesday), getString(R.string.wednesday), getString(R.string.thursday), getString(R.string.friday)};
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
            day = (day == 1) || (day == 7) ? 0 : day - 2;
            binding.viewPager.setCurrentItem(day);
        }
        new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> tab.setText(names[position])).attach();
        viewmodel.getList().observe(getViewLifecycleOwner(), lists -> {
            adapter.setList((List) lists);
            adapter.notifyDataSetChanged();
        });
    }
}
