package pl.vemu.zsme.ui

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.WorkManager
import com.yariksoffice.lingver.Lingver
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MainApplication : Application() {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        Lingver.init(this)
        WorkManager.initialize(
            this, Configuration.Builder()
                .setWorkerFactory(workerFactory)
                .build()
        )
    }

}
