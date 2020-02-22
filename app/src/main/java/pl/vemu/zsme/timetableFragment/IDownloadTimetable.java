package pl.vemu.zsme.timetableFragment;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

import pl.vemu.zsme.STATIC;

public interface IDownloadTimetable {

    default Document getNews(String aurl) throws IOException {
        String url = "https://zsme.tarnow.pl/plan/" + aurl;
        Connection.Response response = Jsoup.connect(url)
                .timeout(30000)
                .method(Connection.Method.GET)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64; rv:41.0) Gecko/20100101 Firefox/41.0")
                .header("Authorization", "Basic " + STATIC.LOGIN)
                .execute();
        return response.parse();
    }
}
