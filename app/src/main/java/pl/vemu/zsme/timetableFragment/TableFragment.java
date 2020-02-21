package pl.vemu.zsme.timetableFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import java.util.List;

import pl.vemu.zsme.R;

public class TableFragment extends Fragment implements IDownloadTable{

    private ViewPager2 viewPager2;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_table, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewPager2 = view.findViewById(R.id.pagerTable);
        String url = TableFragmentArgs.fromBundle(getArguments()).getUrl();
        new DownloadTable(this).execute(url);
    }

    @Override
    public void makeAdapter(List<List<Lesson>> lessons) {
        viewPager2.setAdapter(new TablePageAdapter(this, lessons));
    }
}
