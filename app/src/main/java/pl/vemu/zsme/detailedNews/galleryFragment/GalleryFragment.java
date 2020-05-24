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

import pl.vemu.zsme.databinding.FragmentGalleryBinding;

public class GalleryFragment extends Fragment {

    private FragmentGalleryBinding binding;
    private String[] images;

    public GalleryFragment() {
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        images = GalleryFragmentArgs.fromBundle(getArguments()).getImages();
        ViewPager2 viewPager = (ViewPager2) binding.getRoot();
        viewPager.setAdapter(new GalleryPageAdapter(this, images));
    }
}
