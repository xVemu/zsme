package pl.vemu.zsme.detailedNews.galleryFragment;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.NotNull;

import pl.vemu.zsme.R;
import pl.vemu.zsme.databinding.FragmentGalleryPageBinding;


public class GalleryPageFragment extends Fragment {

    private FragmentGalleryPageBinding binding;
    private String image;
    private boolean isUiVisible = true;

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
        isUiVisible = false;
        setUiVisibility();
        binding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Glide.with(getContext()).load(image).into((PhotoView) binding.getRoot());
        binding.getRoot().setOnClickListener(view1 -> {
            setUiVisibility();
        });
        setUiVisibility();
    }


    // TODO fix appbar when leaving
    private void setUiVisibility() {
        Window window = getActivity().getWindow();
        View decorView = window.getDecorView();
        BottomNavigationView bottom_nav = getActivity().findViewById(R.id.bottom_nav);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (isUiVisible) {
            if (Build.VERSION.SDK_INT >= 30) {
                decorView.getWindowInsetsController().hide(WindowInsets.Type.navigationBars() | WindowInsets.Type.statusBars());
                decorView.getWindowInsetsController().setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_BARS_BY_SWIPE);
            } else {
                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            }
            actionBar.hide();
            bottom_nav.setVisibility(View.GONE);
            isUiVisible = false;
        } else {
            if (Build.VERSION.SDK_INT >= 30) {
                decorView.getWindowInsetsController().show(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
            } else {
                int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    flags |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
                }
                decorView.setSystemUiVisibility(flags);
            }
            actionBar.show();
            bottom_nav.setVisibility(View.VISIBLE);
            isUiVisible = true;
        }
    }
}
