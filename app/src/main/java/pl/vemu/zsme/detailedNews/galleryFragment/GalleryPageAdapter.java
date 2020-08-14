package pl.vemu.zsme.detailedNews.galleryFragment;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

import lombok.RequiredArgsConstructor;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

@RequiredArgsConstructor
public class GalleryPageAdapter extends RecyclerView.Adapter<GalleryPageAdapter.ViewHolder> {

    private final String[] images;
    private final SetUIVisibility setUIVisibility;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PhotoView photoView = new PhotoView(parent.getContext());
        photoView.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        return new ViewHolder(photoView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(holder.photoView.getContext()).load(images[position]).into(holder.photoView);
        holder.photoView.setOnClickListener(view -> setUIVisibility.setUiVisibility());
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
        }
    }
}
