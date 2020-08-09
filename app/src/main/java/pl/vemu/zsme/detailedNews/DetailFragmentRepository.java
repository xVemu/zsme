package pl.vemu.zsme.detailedNews;

import android.text.Spanned;

import androidx.core.text.HtmlCompat;
import androidx.lifecycle.MutableLiveData;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

@Getter
public enum DetailFragmentRepository {
    INSTANCE;

    private final MutableLiveData<Spanned> text = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isRefreshing = new MutableLiveData<>(false);
    private final MutableLiveData<List<String>> images = new MutableLiveData<>(new ArrayList<>());

    private String errorMessage;

    public void downloadText(String url, String errorMessage) {
        this.errorMessage = errorMessage;
        isRefreshing.setValue(true);
        downloadTextThread(url);
    }

    private void downloadTextThread(String url) {
        new Thread(() -> {
            try {
                Document document = Jsoup.connect(url).get();

                Element s = document.selectFirst(".single-post");
                if (s == null) {
                    text.postValue(parseString(errorMessage));
                    return;
                }
                Elements a_img = s.select("a img");
                for (Element element : a_img) {
                    element.parent().unwrap();
                }
                Elements photos = s.select("img");
                if (photos.size() != 0) {
                    ArrayList<String> imagesSrc = new ArrayList<>();
                    for (Element photo : photos) {
                        imagesSrc.add(photo.attr("src").replace("thumbs/thumbs_", ""));
                        photo.remove();
                    }
                    images.postValue(imagesSrc);
                }
                text.postValue(parseString(s.toString()));
            } catch (IOException e) {
                e.printStackTrace();
                text.postValue(parseString(errorMessage));
            }
            isRefreshing.postValue(false);
        }).start();
    }

    //TODO sdk 23 * parse
    private Spanned parseString(String toSpan) {
        return HtmlCompat.fromHtml(toSpan, HtmlCompat.FROM_HTML_MODE_COMPACT);
    }
}
