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
            String hour = element.selectFirst(".g").text();
            for (int i = 0; i < lessons.size(); i++) {
                if (lessons.get(i).selectFirst(".p") == null) continue;
                Element lesson = lessons.get(i);
                Table.TableBuilder lessonBuilder = new Table.TableBuilder();
                Table.TableBuilder lessonBuilder2 = null;
                if ("GD 41 etyka".equals(lesson.text())) {
                    lessonBuilder.name("etyka").room("41").teacher("GD");
                } else if (lesson.select(".p").size() == 2 && "#wf3".equals(lesson.select(".p").get(1).text())) {
                    lessonBuilder.name("wf 3/3").room(lesson.selectFirst(".s").text());
                } else if (lesson.getElementsByAttributeValue("style", "font-size:85%").size() == 2) {
                    Elements spans = lesson.getElementsByAttributeValue("style", "font-size:85%");
                    lessonBuilder = buildLesson(spans.first());
                    lessonBuilder2 = buildLesson(spans.last());
                    String[] split = hour.replaceAll("\\s+", "").split("-");
                    lessonBuilder2.index(index).timeStart(split[0]).timeFinish(split[1]);
                } else {
                    lessonBuilder = buildLesson(lesson);
                }
                String[] split = hour.replaceAll("\\s+", "").split("-");
                lessonBuilder.index(index).timeStart(split[0]).timeFinish(split[1]);
                lessonsList.get(i).add(lessonBuilder.build());
                if (lessonBuilder2 != null) lessonsList.get(i).add(lessonBuilder2.build());
            }
        }
        return lessonsList;
    }

    private Table.TableBuilder buildLesson(Element lesson) {
        return Table.builder()
                .name(lesson.select(".p").text())
                .teacher(lesson.selectFirst(".n") == null ? (lesson.selectFirst(".o") == null ? "" : lesson.selectFirst(".o").text()) : lesson.selectFirst(".n").text())
                .room(lesson.selectFirst(".s") == null ? lesson.selectFirst(".o").text() : lesson.selectFirst(".s").text());
    }
}
