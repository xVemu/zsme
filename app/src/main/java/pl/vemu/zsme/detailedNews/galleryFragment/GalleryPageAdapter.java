package pl.vemu.zsme.detailedNews.galleryFragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class GalleryPageAdapter extends FragmentStateAdapter {

    private final String[] images;

    public GalleryPageAdapter(@NonNull Fragment fragment, String[] images) {
        super(fragment);
        this.images = images;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return new GalleryPageFragment(images[position]);
    }

    @Override
    public int getItemCount() {
        return images.length;
    }
}
