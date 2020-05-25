package pl.vemu.zsme.newsFragment;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

import pl.vemu.zsme.MainActivity;
import pl.vemu.zsme.R;

public class NewsWorker extends Worker {

    public NewsWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            Document document = Jsoup.connect("https://zsme.tarnow.pl/").get();
            if (document.selectFirst(".column-one") == null) return Result.failure();
            NewsItem downloaded = NewsItem.makeNewsItem(document.selectFirst(".column-one").child(0));
            SharedPreferences memory = getApplicationContext().getSharedPreferences("savedValue", Context.MODE_PRIVATE);
            String fromMemory = memory.getString("article-title", null);
            if (fromMemory == null) {
                SharedPreferences.Editor editor = memory.edit();
                editor.putString("article-title", downloaded.getTitle()).apply();
                return Result.retry();
            } else if (fromMemory.equals(downloaded.getTitle())) return Result.failure();
            else {
                SharedPreferences.Editor editor = memory.edit();
                editor.putString("article-title", downloaded.getTitle()).apply();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("NewsItem", downloaded);
                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "ZSME")
                        .setSmallIcon(R.drawable.zsme)
                        .setContentTitle(downloaded.getTitle())
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(downloaded.getDescription()))
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_MIN);
                NotificationManagerCompat.from(getApplicationContext()).notify(0, builder.build());
                return Result.success();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Result.failure();
    }
}
