package pl.vemu.zsme.newsFragment;

import org.jsoup.nodes.Element;

public class NewsItem {

    private final String title, description, imgUrl, url, author, date;

    public NewsItem(String title, String description, String imgUrl, String url, String author, String date) {
        this.title = title;
        this.description = description;
        this.imgUrl = imgUrl;
        this.url = url;
        this.author = author;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getUrl() {
        return url;
    }

    public String getAuthor() {
        return author;
    }

    public String getDate() {
        return date;
    }

    static NewsItem makeNewsItem(Element element) {
        NewsItem.NewsItemBuilder newsItemBuilder = NewsItem.builder();
        if (element.selectFirst(".article-entry") != null) {
            newsItemBuilder.title(element.selectFirst(".article-title").text())
                    .description(element.selectFirst(".article-entry").text())
                    .author(element.selectFirst(".article-author").text())
                    .date(element.selectFirst(".article-date").text())
                    .imgUrl(element.selectFirst("img") == null ? null : element.selectFirst("img").attr("src"))
                    .url(element.selectFirst("a").attr("href"));
            return newsItemBuilder.build();
        }
        return null;
    }

    public static NewsItemBuilder builder() {
        return new NewsItemBuilder();
    }

    public static class NewsItemBuilder {
        private String title, description, imgUrl, url, author, date;

        public NewsItemBuilder title(String title) {
            this.title = title;
            return this;
        }

        public NewsItemBuilder description(String description) {
            this.description = description;
            return this;
        }

        public NewsItemBuilder imgUrl(String imgUrl) {
            this.imgUrl = imgUrl;
            return this;
        }

        public NewsItemBuilder url(String url) {
            this.url = url;
            return this;
        }

        public NewsItemBuilder author(String author) {
            this.author = author;
            return this;
        }

        public NewsItemBuilder date(String date) {
            this.date = date;
            return this;
        }

        public NewsItem build() {
            return new NewsItem(title, description, imgUrl, url, author, date);
        }
    }
}
