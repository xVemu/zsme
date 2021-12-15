package pl.vemu.zsme.ui.post

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC
import androidx.core.app.NotificationManagerCompat
import androidx.core.text.HtmlCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import pl.vemu.zsme.R
import pl.vemu.zsme.data.db.PostDAO
import pl.vemu.zsme.data.service.ZSMEService
import pl.vemu.zsme.ui.MainActivity
import pl.vemu.zsme.util.mappers.PostMapper

@HiltWorker
class PostWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val postDAO: PostDAO,
    private val zsmeService: ZSMEService,
    private val postMapper: PostMapper
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result =
        coroutineScope {
            val postModels = postDAO.getAll()
            val response = zsmeService.searchPosts("", 1, 20)
            val postModelsFromService = postMapper.mapFromEntityList(response)
            if (postModels.first() == postModelsFromService[0]) return@coroutineScope Result.failure()
            for (item in postModelsFromService) {
                if (item == postModels.first()) break
                val intent = Intent(context, MainActivity::class.java).apply {
                    data = Uri.parse("zsme://detail/${item.id}")
                    flags = (Intent.FLAG_ACTIVITY_NEW_TASK
                            or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                }
                val pendingIntent =
                    PendingIntent.getActivity(
                        context,
                        item.hashCode(),
                        intent,
                        FLAG_IMMUTABLE
                    )
                val builder =
                    NotificationCompat.Builder(
                        context,
                        context.getString(R.string.app_name)
                    )
                        .setSmallIcon(R.drawable.ic_news)
                        .setContentTitle(item.title)
                        .setContentText(
                            HtmlCompat.fromHtml(
                                item.excerpt,
                                HtmlCompat.FROM_HTML_MODE_COMPACT
                            )
                        )
                        .setStyle(
                            NotificationCompat.BigTextStyle().bigText(
                                HtmlCompat.fromHtml(
                                    item.excerpt,
                                    HtmlCompat.FROM_HTML_MODE_COMPACT
                                )
                            )
                        )
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .setVisibility(VISIBILITY_PUBLIC)
                        .setPriority(NotificationCompat.PRIORITY_LOW)
                NotificationManagerCompat.from(context)
                    .notify(item.hashCode(), builder.build())
                postDAO.insert(item)
            }
            Result.success()
        }
}