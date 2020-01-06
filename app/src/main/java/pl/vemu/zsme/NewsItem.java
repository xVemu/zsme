package pl.vemu.zsme;

public class NewsItem {

    private final String title;
    private final String description;
    private final String imgUrl;

    NewsItem(String title, String description, String imgUrl) {
        this.title = title;
        this.description = description;
        this.imgUrl = imgUrl;
    }

    String getTitle() {
        return title;
    }

    String getDescription() {
        return description;
    }

    String getImgUrl() {
        return imgUrl;
    }

}
