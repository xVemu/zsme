package pl.vemu.zsme.newsFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.vemu.zsme.R;

@RequiredArgsConstructor
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item, parent, false);
        return new NewsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsHolder holder, int position) {
        NewsItem item = newsItems.get(position);
        holder.title.setText(item.getTitle());
        holder.description.setText(item.getDescription());
        holder.author.setText(item.getAuthor());
        holder.date.setText(item.getDate());
        NewsFragmentDirections.ActionNewsToDetailFragment actionNewsToDetailFragment = NewsFragmentDirections.actionNewsToDetailFragment(item);
        holder.itemView.setOnClickListener(Navigation.createNavigateOnClickListener(actionNewsToDetailFragment));
        Picasso.get().load(item.getImgUrl()).resize(1080, 1080).centerCrop().into(holder.img);
    }

    @Override
    public int getItemCount() {
        return newsItems.size();
    }

    class NewsHolder extends RecyclerView.ViewHolder {

        final TextView title, description, author, date;
        final ImageView img;

        NewsHolder(@NonNull View itemView) {
            super(itemView);
            description = itemView.findViewById(R.id.description);
            title = itemView.findViewById(R.id.title);
            author = itemView.findViewById(R.id.author);
            date = itemView.findViewById(R.id.date);
            img = itemView.findViewById(R.id.thumbnail);
        }
    }
}
