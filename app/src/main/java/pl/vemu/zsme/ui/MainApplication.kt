package pl.vemu.zsme.ui

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androix.startup.KoinStartup.onKoinStartup
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.ksp.generated.defaultModule
import org.koin.ksp.generated.module
import pl.vemu.zsme.injection.RetrofitModule
import pl.vemu.zsme.injection.RoomModule

class MainApplication : Application() {
    init {
        @Suppress("OPT_IN_USAGE")
        onKoinStartup {
            androidContext(this@MainApplication)
            modules(
                RetrofitModule().module,
                RoomModule().module,
            )
            androidLogger()
            defaultModule()
        }
    }
}
