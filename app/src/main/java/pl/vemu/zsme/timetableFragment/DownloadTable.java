package pl.vemu.zsme.timetableFragment;

import android.os.AsyncTask;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DownloadTable extends AsyncTask<String, Void, List<List<Lesson>>> implements TimetableDownload {

    private final TableDownload context;

    @Override
    protected List<List<Lesson>> doInBackground(String... strings) {
        try {
            Document document = getNews(strings[0]);
            Elements table = document.selectFirst(".tabela").child(0).children();
            table.remove(0);
            List<List<Lesson>> lessonsList = Arrays.asList(new ArrayList<>(),
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
                    Lesson.LessonBuilder lessonBuilder = new Lesson.LessonBuilder();
                    Lesson.LessonBuilder lessonBuilder2 = null;
                    if ("GD 41 etyka".equals(lesson.text())) {
                        lessonBuilder.name("etyka").room("41").teacher("GD");
                    } else if (lesson.select(".p").size() == 2 && "#wf3".equals(lesson.select(".p").get(1).text())) {
                        lessonBuilder.name("wf 3/3").room(lesson.selectFirst(".s").text());
                    } else if (lesson.getElementsByAttributeValue("style", "font-size:85%").size() == 2) {
                        Elements spans = lesson.getElementsByAttributeValue("style", "font-size:85%");
                        lessonBuilder = buildLesson(spans.first());
                        lessonBuilder2 = buildLesson(spans.last());
                        lessonBuilder2.index(index).hour(hour);
                    } else {
                        lessonBuilder = buildLesson(lesson);
                    }
                    lessonBuilder.index(index).hour(hour);
                    lessonsList.get(i).add(lessonBuilder.build());
                    if (lessonBuilder2 != null) lessonsList.get(i).add(lessonBuilder2.build());
                }
            }
            return lessonsList;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Lesson.LessonBuilder buildLesson(Element lesson) {
        return Lesson.builder()
                .name(lesson.selectFirst(".p").text())
                .teacher(lesson.selectFirst(".n") == null ? lesson.selectFirst(".o").text() : lesson.selectFirst(".n").text())
                .room(lesson.selectFirst(".s") == null ? lesson.selectFirst(".o").text() : lesson.selectFirst(".s").text());
    }

    @Override
    protected void onPostExecute(List<List<Lesson>> lessons) {
        context.makeAdapter(lessons);
    }
}
