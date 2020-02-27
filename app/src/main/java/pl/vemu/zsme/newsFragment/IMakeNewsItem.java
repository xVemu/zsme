package pl.vemu.zsme.newsFragment;

import org.jsoup.nodes.Element;

public interface IMakeNewsItem {

    default NewsItem makeNewsItem(Element element) {
        NewsItem.NewsItemBuilder newsItemBuilder = NewsItem.builder();
        newsItemBuilder.title(element.selectFirst(".article-title").text())
                .description(element.selectFirst(".article-entry").text())
                .imgUrl(element.selectFirst("img").attr("src"))
                .url(element.selectFirst("a").attr("href"));
        return newsItemBuilder.build();
    }
}
