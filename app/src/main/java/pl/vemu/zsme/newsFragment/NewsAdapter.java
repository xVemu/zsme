package pl.vemu.zsme.newsFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import pl.vemu.zsme.R;
import pl.vemu.zsme.databinding.ItemNewsBinding;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsHolder> {

    @Getter
    private final List<NewsItem> newsItems = new ArrayList<>();

    void addNewsItem(NewsItem item) {
        newsItems.add(item);
    }

    void removeAllItems() {
        newsItems.clear();
    }

    @NonNull
    @Override
    public NewsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news, parent, false);
        return new NewsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsHolder holder, int position) {
        NewsItem item = newsItems.get(position);
        if (item == null) return;
        Glide.with(holder.img.getContext()).load(item.getImgUrl()).centerCrop().into(holder.img);
        holder.title.setText(item.getTitle());
        holder.description.setText(item.getDescription());
        holder.author.setText(item.getAuthor());
        holder.date.setText(item.getDate());
        NewsFragmentDirections.ActionNewsToDetailFragment actionNewsToDetailFragment = NewsFragmentDirections.actionNewsToDetailFragment(item);
        holder.itemView.setOnClickListener(Navigation.createNavigateOnClickListener(actionNewsToDetailFragment));
//        setFadeAnimation(holder.itemView);
    }

    private void setFadeAnimation(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(1000);
        view.startAnimation(anim);
    }

    @Override
    public int getItemCount() {
        return newsItems.size();
    }

    static class NewsHolder extends RecyclerView.ViewHolder {

        final TextView title, description, author, date;
        final ImageView img;

        NewsHolder(@NonNull View itemView) {
            super(itemView);
            ItemNewsBinding binding = ItemNewsBinding.bind(itemView);
            description = binding.description;
            title = binding.title;
            img = binding.thumbnail;
            author = binding.author;
            date = binding.date;
        }
    }
}
