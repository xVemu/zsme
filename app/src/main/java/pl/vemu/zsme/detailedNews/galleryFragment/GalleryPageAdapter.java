package pl.vemu.zsme.detailedNews.galleryFragment;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.chrisbanes.photoview.PhotoView;

import coil.Coil;
import coil.request.ImageRequest;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class GalleryPageAdapter extends RecyclerView.Adapter<GalleryPageAdapter.ViewHolder> {

    private final String[] images;
    private static SwitchUIVisibility switchUIVisibility;

    public static void setSwitchUIVisibility(SwitchUIVisibility switchUIVisibility) {
        GalleryPageAdapter.switchUIVisibility = switchUIVisibility;
    }

    public GalleryPageAdapter(String[] images) {
        this.images = images;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PhotoView photoView = new PhotoView(parent.getContext());
        photoView.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        return new ViewHolder(photoView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ImageRequest request = new ImageRequest.Builder(holder.photoView.getContext())
                .data(images[position])
                .crossfade(true)
                .target(holder.photoView)
                .build();
        Coil.imageLoader(holder.photoView.getContext()).enqueue(request);
    }

    @Override
    public int getItemCount() {
        return images.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final PhotoView photoView;

        public ViewHolder(@NonNull PhotoView itemView) {
            super(itemView);
            this.photoView = itemView;
            this.photoView.setOnClickListener(view -> switchUIVisibility.switchUiVisibility());
        }
    }
}
