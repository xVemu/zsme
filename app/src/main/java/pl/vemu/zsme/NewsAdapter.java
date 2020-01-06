package pl.vemu.zsme;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsHolder> {

    final ArrayList<NewsItem> newsItems;

    NewsAdapter(ArrayList<NewsItem> newsItems) {
        this.newsItems = newsItems;
    }

    @NonNull
    @Override
    public NewsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item, parent, false);
        return new NewsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsHolder holder, int position) {
        NewsItem item = newsItems.get(position);
        holder.title.setText(item.getTitle());
        holder.description.setText(item.getDescription());
        Picasso.get().load(item.getImgUrl()).resize(1080, 1080).centerCrop().into(holder.img);
    }

    @Override
    public int getItemCount() {
        return newsItems.size();
    }

    class NewsHolder extends RecyclerView.ViewHolder {

        final TextView title;
        final TextView description;
        final ImageView img;

        NewsHolder(@NonNull View itemView) {
            super(itemView);
            description = itemView.findViewById(R.id.description);
            title = itemView.findViewById(R.id.title);
            img = itemView.findViewById(R.id.thumbnail);
        }
    }
}
