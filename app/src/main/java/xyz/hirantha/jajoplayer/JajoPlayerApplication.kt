package xyz.hirantha.jajoplayer

import androidx.multidex.MultiDexApplication
import com.jakewharton.threetenabp.AndroidThreeTen
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule

class JajoPlayerApplication : MultiDexApplication(), KodeinAware {

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
    }

    override val kodein: Kodein = Kodein.lazy {
        import(androidXModule(this@JajoPlayerApplication))


    }
}