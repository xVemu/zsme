package pl.vemu.zsme.newsFragment;

import java.io.Serializable;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class NewsItem implements Serializable {
    private String title, description, author, date, imgUrl, url;
}
