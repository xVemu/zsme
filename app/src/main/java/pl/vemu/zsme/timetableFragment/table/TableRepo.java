package pl.vemu.zsme.timetableFragment.table;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pl.vemu.zsme.timetableFragment.Login;

public enum TableRepo {
    INSTANCE;

    public List<List<Table>> downloadTimetable(String url) throws IOException {
        Document document = Login.INSTANCE.login(url);
        Elements table = document.selectFirst(".tabela").child(0).children();
        table.remove(0);
        List<List<Table>> lessonsList = Arrays.asList(new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>());
        for (Element element : table) {
            Elements lessons = element.select(".l");
            String index = element.selectFirst(".nr").text();
            String[] hour = element.selectFirst(".g").text().replaceAll("\\s+", "").split("-");
            for (int i = 0; i < lessons.size(); i++) {
                Element lesson = lessons.get(i);
                if (lesson.text().isEmpty()) continue;
                Table.TableBuilder lessonBuilder;
                Table.TableBuilder lessonBuilder2 = null;
                if (lesson.getElementsByAttributeValue("style", "font-size:85%").size() > 1) {
                    Elements spans = lesson.getElementsByAttributeValue("style", "font-size:85%");
                    lessonBuilder = buildLesson(spans.first());
                    lessonBuilder2 = buildLesson(spans.get(1));
                    lessonBuilder2.index(index).timeStart(hour[0]).timeFinish(hour[1]);
                } else if (lesson.selectFirst("br") != null) {
                    lessonBuilder = buildLesson(lesson.selectFirst("[style=font-size:85%]"));
                    lesson.child(0).remove();
                    lesson.child(0).remove();
                    lessonBuilder2 = buildLesson(lesson);
                    lessonBuilder2.index(index).timeStart(hour[0]).timeFinish(hour[1]);
                } else {
                    lessonBuilder = buildLesson(lesson);
                }
                lessonBuilder.index(index).timeStart(hour[0]).timeFinish(hour[1]);
                lessonsList.get(i).add(lessonBuilder.build());
                if (lessonBuilder2 != null) lessonsList.get(i).add(lessonBuilder2.build());
            }
        }
        return lessonsList;
    }

    private Table.TableBuilder buildLesson(Element lesson) {
        return Table.builder()
                .name(lesson.selectFirst(".p") != null ? lesson.selectFirst(".p").text() : lesson.text())
                .teacher(lesson.selectFirst(".n") != null ? lesson.selectFirst(".n").text() : (lesson.selectFirst(".o") != null ? lesson.selectFirst(".o").text() : ""))
                .room(lesson.selectFirst(".s") != null ? lesson.selectFirst(".s").text() : "");
    }
}
