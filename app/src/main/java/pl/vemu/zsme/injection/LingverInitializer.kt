package pl.vemu.zsme.injection

import android.app.Application
import android.content.Context
import androidx.startup.Initializer
import com.yariksoffice.lingver.Lingver

class LingverInitializer : Initializer<Lingver> {
    override fun create(context: Context) = Lingver.init(context.applicationContext as Application)

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}
