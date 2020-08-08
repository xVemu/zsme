package pl.vemu.zsme.detailedNews.galleryFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import org.jetbrains.annotations.NotNull;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class GalleryFragment extends Fragment {

    private ViewPager2 pager;

    public GalleryFragment() {
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        pager = new ViewPager2(getContext());
        pager.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        return pager;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        String[] images = GalleryFragmentArgs.fromBundle(getArguments()).getImages();
        pager.setAdapter(new GalleryPageAdapter(images));
    }
}
