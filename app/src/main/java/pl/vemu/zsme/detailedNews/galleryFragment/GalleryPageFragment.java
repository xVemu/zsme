package pl.vemu.zsme.detailedNews.galleryFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

import org.jetbrains.annotations.NotNull;

import pl.vemu.zsme.databinding.FragmentGalleryPageBinding;


public class GalleryPageFragment extends Fragment {

    private FragmentGalleryPageBinding binding;
    private String image;

    public GalleryPageFragment() {
    }

    public GalleryPageFragment(String image) {
        this.image = image;
    }


    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentGalleryPageBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Glide.with(getContext()).load(image).into((PhotoView) binding.getRoot());
    }
}
