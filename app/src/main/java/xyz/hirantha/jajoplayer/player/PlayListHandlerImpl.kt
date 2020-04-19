package xyz.hirantha.jajoplayer.player

import android.content.Context
import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import xyz.hirantha.jajoplayer.data.repository.MediaRepository
import xyz.hirantha.jajoplayer.internal.lazyDeferred
import xyz.hirantha.jajoplayer.models.Song
import kotlin.coroutines.CoroutineContext

class PlayListHandlerImpl(
    private val context: Context,
    private val mediaRepository: MediaRepository
) : PlayListHandler, KodeinAware, CoroutineScope {

    private var job: Job = Job()
    override val coroutineContext: CoroutineContext get() = job + Dispatchers.Main
    override val kodein: Kodein by closestKodein(context)
    private val _songs by lazyDeferred { mediaRepository.getSongs() }
    private var songs: List<Song>? = null
    private var currentSongId = -1
    override val currentSong: LiveData<Song> get() = _currentSong
    private val _currentSong = MutableLiveData<Song>()

    init {
        observeSongs()
    }

    private fun observeSongs() = launch {
        _songs.await().observeForever {
            if (it == null) return@observeForever
            songs = it
        }
    }

    override fun onCompletion(mp: MediaPlayer?) {
        currentSongId++
        if (currentSongId == songs?.size) currentSongId = 0
        songs?.let {
            _currentSong.postValue(it[currentSongId])
            mp?.reset()
            mp?.setDataSource(context, it[currentSongId].getUri())
            mp?.prepare()
            mp?.start()
        }
    }

    override fun onDestroy() {
        job.cancel()
    }

    override fun playSong(song: Song, mp: MediaPlayer) {
        songs?.let {
            if (currentSongId == it.indexOf(song)) return
            currentSongId = it.indexOf(song)
            if (currentSongId == -1) currentSongId = 0
            _currentSong.postValue(it[currentSongId])
            mp.reset()
            mp.setDataSource(context, it[currentSongId].getUri())
            mp.prepare()
            mp.start()
        }
    }
}