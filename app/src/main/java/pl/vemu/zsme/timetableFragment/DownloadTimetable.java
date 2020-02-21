package pl.vemu.zsme.timetableFragment;

import android.os.AsyncTask;

import org.apache.commons.collections4.map.LinkedMap;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class DownloadTimetable extends AsyncTask<Void, Void, List<LinkedMap<String, String>>> implements IDownloadTimetable{

    private final ISetMaps context;

    @Override
    protected List<LinkedMap<String, String>> doInBackground(Void... voids) {
        try {
            Document document = getNews("lista.html");
            List<Elements> elements = Arrays.asList(document.selectFirst("#oddzialy").children(),
                    document.selectFirst("#nauczyciele").children(),
                    document.selectFirst("#sale").children());
            List<LinkedMap<String, String>> maps = Arrays.asList(new LinkedMap<>(),
                    new LinkedMap<>(),
                    new LinkedMap<>());
            for (int i = 0; i < elements.size(); i++) {
                for (Element element : elements.get(i)) {
                    maps.get(i).put(element.text(), element.child(0).attr("href"));
                }
            }
            return maps;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<LinkedMap<String, String>> maps) {
        super.onPostExecute(maps);
        context.makePageAdapter(maps);
    }
}
