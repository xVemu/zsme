package pl.vemu.zsme.timetableFragment.timetable;

import androidx.lifecycle.MutableLiveData;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pl.vemu.zsme.timetableFragment.Login;

public enum DownloadTimetable {
    INSTANCE;

    private final MutableLiveData<List<List<Timetable>>> list = new MutableLiveData<>(new ArrayList<>());

    public void downloadTimetable() {
        new Thread(() -> {
            try {
                Document document = Login.INSTANCE.login("lista.html");
                List<Elements> elements = new ArrayList<>();
                for (Element ul : document.select("ul")) {
                    elements.add(ul.children());
                }
            /*List<Elements> elements = Arrays.asList(document.selectFirst("#oddzialy").children(),
                    document.selectFirst("#nauczyciele").children(),
                    document.selectFirst("#sale").children());*/
                List<List<Timetable>> maps = Arrays.asList(new ArrayList<>(),
                        new ArrayList<>(),
                        new ArrayList<>());
                for (int i = 0; i < elements.size(); i++) {
                    for (Element element : elements.get(i)) {
                        maps.get(i).add(new Timetable(element.text(), element.child(0).attr("href")));
                    }
                }
                list.postValue(maps);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public MutableLiveData<List<List<Timetable>>> getList() {
        return list;
    }
}
