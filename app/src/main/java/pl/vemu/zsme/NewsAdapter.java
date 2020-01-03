package pl.vemu.zsme;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsHolder> {

    ArrayList<NewsItem> newsItems;


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

    }

    @Override
    public int getItemCount() {
        return newsItems.size();
    }

    class NewsHolder extends RecyclerView.ViewHolder {
        public NewsHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
