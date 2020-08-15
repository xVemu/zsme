package pl.vemu.zsme.detailedNews;

import android.text.Spanned;

import androidx.core.text.HtmlCompat;

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
        detail.setText(parseString(s.toString()));
        return detail;
    }

    //TODO sdk 23 * parse
    private Spanned parseString(String toSpan) {
        return HtmlCompat.fromHtml(toSpan, HtmlCompat.FROM_HTML_MODE_COMPACT);
    }
}
