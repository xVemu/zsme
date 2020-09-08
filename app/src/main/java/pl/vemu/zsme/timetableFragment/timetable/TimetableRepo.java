package pl.vemu.zsme.timetableFragment.timetable;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pl.vemu.zsme.timetableFragment.Login;

public enum TimetableRepo {
    INSTANCE;

    public List<List<Timetable>> downloadTimetable() throws IOException {
        Document document = Login.INSTANCE.login("lista.html");
        List<Elements> elements = Arrays.asList(document.selectFirst("#oddzialy").children(),
                document.selectFirst("#nauczyciele").children(),
                document.selectFirst("#sale").children());
        List<List<Timetable>> maps = Arrays.asList(new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>());
        for (int i = 0; i < elements.size(); i++) {
            for (Element element : elements.get(i)) {
                maps.get(i).add(new Timetable(element.text(), element.child(0).attr("href")));
            }
        }
        return maps;
    }
}
