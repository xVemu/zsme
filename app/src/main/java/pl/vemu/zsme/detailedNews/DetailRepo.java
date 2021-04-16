package pl.vemu.zsme.detailedNews;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import lombok.Getter;

@Getter
public enum DetailRepo {
    INSTANCE;

    public Detail downloadText(String url) throws IOException {
        Document document = Jsoup.connect(url).get();
        Element toRemove = document.selectFirst(".heateor_sss_sharing_container");
        if (toRemove != null) toRemove.remove();
        Element s = document.selectFirst(".single-post");
        if (s == null) {
            return null;
        }
        Elements a_img = s.select("a img");
        for (Element element : a_img) {
            element.parent().unwrap();
        }
        Elements photos = s.select("img");
        Detail detail = new Detail();
        if (photos.size() != 0) {
            ArrayList<String> imagesSrc = new ArrayList<>();
            for (Element photo : photos) {
                imagesSrc.add(photo.attr("src").replace("thumbs/thumbs_", ""));
                photo.remove();
            }
            detail.setImages(imagesSrc);
        }
        detail.setHtml(s.toString());
        return detail;
    }
}
