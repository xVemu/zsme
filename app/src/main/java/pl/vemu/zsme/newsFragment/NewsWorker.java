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

import java.io.IOException;
import java.util.List;

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
            List<NewsItem> downloaded = DownloadNews.INSTANCE.downloadNews(new Queries.Page(), 1);
            NewsItem firstNewsItem = downloaded.get(0);
            SharedPreferences memory = getApplicationContext().getSharedPreferences("latest-news", Context.MODE_PRIVATE);
            int fromMemory = memory.getInt("article", 0);
            if (fromMemory == 0) {
                SharedPreferences.Editor editor = memory.edit();
                editor.putInt("article", firstNewsItem.hashCode()).apply();
                return Result.retry();
            } else if (fromMemory == firstNewsItem.hashCode())
                return Result.failure();
            else {
                SharedPreferences.Editor editor = memory.edit();
                for (NewsItem item : downloaded) {
                    if (fromMemory == item.hashCode()) break;
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("url", item.getUrl());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK
                            | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), item.hashCode(), intent, 0);
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "ZSME")
                            .setSmallIcon(R.drawable.ic_news)
                            .setContentTitle(item.getTitle())
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(item.getDescription()))
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true)
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                    NotificationManagerCompat.from(getApplicationContext()).notify(item.hashCode(), builder.build());
                }
                editor.putInt("article", firstNewsItem.hashCode()).apply();
                return Result.success();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Result.failure();
    }
}
