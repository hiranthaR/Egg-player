package xyz.hirantha.jajoplayer

import androidx.multidex.MultiDexApplication
import com.jakewharton.threetenabp.AndroidThreeTen
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton
import xyz.hirantha.jajoplayer.data.db.AppDatabase
import xyz.hirantha.jajoplayer.data.providers.PlayerStateProvider
import xyz.hirantha.jajoplayer.data.providers.PlayerStateProviderImpl
import xyz.hirantha.jajoplayer.data.repository.MediaRepository
import xyz.hirantha.jajoplayer.data.repository.MediaRepositoryImpl
import xyz.hirantha.jajoplayer.data.repository.Repository
import xyz.hirantha.jajoplayer.data.repository.RepositoryImpl
import xyz.hirantha.jajoplayer.player.JajoPlayer
import xyz.hirantha.jajoplayer.player.PlaylistHandler
import xyz.hirantha.jajoplayer.player.PlaylistHandlerImpl
import xyz.hirantha.jajoplayer.ui.home.HomeViewModelFactory

class JajoPlayerApplication : MultiDexApplication(), KodeinAware {

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
    }

    override val kodein: Kodein = Kodein.lazy {
        import(androidXModule(this@JajoPlayerApplication))

        // database
        bind() from singleton { AppDatabase(instance()) }
        bind() from singleton { instance<AppDatabase>().getSongsDao() }

        // providers
        bind<PlayerStateProvider>() with singleton { PlayerStateProviderImpl(instance()) }

        bind<MediaRepository>() with singleton {
            MediaRepositoryImpl(instance(), instance())
        }
        bind<Repository>() with singleton {
            RepositoryImpl(instance())
        }

        bind<PlaylistHandler>() with singleton {
            PlaylistHandlerImpl(
                instance(),
                instance(),
                instance()
            )
        }
        bind() from singleton { JajoPlayer(instance(), instance(), instance()) }

        bind() from provider { HomeViewModelFactory(instance(), instance()) }
    }
}