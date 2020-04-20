package xyz.hirantha.jajoplayer.player

import android.content.Context
import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import xyz.hirantha.jajoplayer.data.repository.Repository
import xyz.hirantha.jajoplayer.internal.getUri
import xyz.hirantha.jajoplayer.models.Song
import xyz.hirantha.jajoplayer.notification.NotificationManager

class JajoPlayer(
    private val context: Context,
    private val playlistHandler: PlaylistHandler,
    private val repository: Repository,
    private val notificationManager: NotificationManager
) :
    MediaPlayer.OnCompletionListener {

    val playing: LiveData<Boolean> get() = _playing
    private val _playing = MutableLiveData<Boolean>(false)

    val currentSong: LiveData<Song> get() = _currentSong
    private val _currentSong = MutableLiveData<Song>()

    init {
        playlistHandler.songs.observeForever {
            if (isPlaying()) return@observeForever
            if (it == null) return@observeForever
            val lastPlayedSong = repository.getLastPlayedSong()
            if (lastPlayedSong != null) {
                if (playlistHandler.bringSongToFront(lastPlayedSong)) {
                    initSong(lastPlayedSong)
                } else {
                    playlistHandler.nextSong()?.let { song -> initSong(song) }
                }
            } else {
                playlistHandler.nextSong()?.let { song -> initSong(song) }
            }
        }
    }

    private var mediaPlayer: MediaPlayer = MediaPlayer().apply {
        setOnCompletionListener(this@JajoPlayer)
    }

    fun playSong(song: Song) {
        playlistHandler.bringSongToFront(song)
        initSong(song)
        start()
    }

    fun playNextSong() {
        val nextSong = playlistHandler.nextSong()
        nextSong?.let {
            initSong(it)
            start()
        }
    }


    fun playPreviousSong() {
        val previousSong = playlistHandler.previousSong()
        previousSong?.let {
            initSong(it)
            start()
        }
    }


    private fun initSong(song: Song) {
        notificationManager.initSong(song)
        repository.setLastPlayedSong(song)
        _currentSong.postValue(song)
        mediaPlayer.reset()
        mediaPlayer.setDataSource(context, song.getUri())
        mediaPlayer.prepare()
    }

    fun start() {
        mediaPlayer.start()
        notificationManager.sendNotification(false)
        _playing.postValue(true)
    }

    fun stop() {
        mediaPlayer.stop()
        _playing.postValue(false)
    }

    fun pause() {
        mediaPlayer.pause()
        notificationManager.sendNotification(true)
        _playing.postValue(false)
    }

    fun isPlaying() = mediaPlayer.isPlaying
    fun getDuration() = mediaPlayer.duration
    fun getCurrentPosition() = mediaPlayer.currentPosition
    fun seekTo(position: Int) = mediaPlayer.seekTo(position)

    override fun onCompletion(mp: MediaPlayer?) {
        val song = playlistHandler.nextSong()
        song?.let {
            initSong(it)
            start()
        }
    }
}

