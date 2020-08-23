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
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.NotNull;

import pl.vemu.zsme.R;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class GalleryFragment extends Fragment implements SetUIVisibility {

    private ViewPager2 pager;
    private boolean isUiVisible = true;

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
        GalleryPageAdapter.setSetUIVisibility(this);
        pager.setAdapter(new GalleryPageAdapter(images));
        setUiVisibility();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isUiVisible = false;
        setUiVisibility();
    }

    @Override
    public void setUiVisibility() {
        Window window = requireActivity().getWindow();
        View decorView = window.getDecorView();
        BottomNavigationView bottom_nav = requireActivity().findViewById(R.id.bottom_nav);
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (isUiVisible) {
            if (Build.VERSION.SDK_INT >= 30) {
                decorView.getWindowInsetsController().hide(WindowInsets.Type.navigationBars() | WindowInsets.Type.statusBars());
                decorView.getWindowInsetsController().setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_BARS_BY_SWIPE);
            } else {
                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE
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
                int flags = 0;
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
