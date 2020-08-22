package pl.vemu.zsme.newsFragment;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public enum DownloadNews {
    INSTANCE;

    public List<NewsItem> downloadNews(Queries query, int page) throws IOException {
        String url = "https://zsme.tarnow.pl/wp/" + query.parseUrl(page).replaceAll(" ", "+");
        Document document = Jsoup.connect(url).get();
        if (document.selectFirst(".column-one") == null) {
            throw new IOException("Not found");
        }
        List<NewsItem> newsItems = new ArrayList<>();
        if (document.selectFirst(".single-post .article-entry") != null) {
            newsItems.add(new NewsItem.NewsItemBuilder()
                    .url("https://zsme.tarnow.pl")
                    .description(document.selectFirst(".single-post .article-entry").text().substring(0, 300) + "...").build());
        }
        Elements columnOneNews = document.selectFirst(".column-one").children();
        Elements columnTwoNews = document.selectFirst(".column-two").children();
        for (int i = 0; i < columnOneNews.size(); i++) {
            newsItems.add(NewsItem.makeNewsItem(columnOneNews.get(i)));
            if (columnTwoNews.get(i).selectFirst(".article-title") != null)
                newsItems.add(NewsItem.makeNewsItem(columnTwoNews.get(i)));
        }
        return newsItems;
    }
}
