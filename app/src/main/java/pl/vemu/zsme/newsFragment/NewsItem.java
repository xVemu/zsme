package pl.vemu.zsme.newsFragment;

import org.jsoup.nodes.Element;

import java.io.Serializable;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class NewsItem implements Serializable {

    private String title, description, imgUrl, url, author, date;

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
}
