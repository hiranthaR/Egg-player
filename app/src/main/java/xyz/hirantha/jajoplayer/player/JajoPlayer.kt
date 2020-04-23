package xyz.hirantha.jajoplayer.player

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import xyz.hirantha.jajoplayer.data.repository.Repository
import xyz.hirantha.jajoplayer.internal.Repeat
import xyz.hirantha.jajoplayer.internal.getUri
import xyz.hirantha.jajoplayer.models.Song
import xyz.hirantha.jajoplayer.notification.NotificationManager

class JajoPlayer(
    private val context: Context,
    private val playlistHandler: PlaylistHandler,
    private val repository: Repository,
    private val notificationManager: NotificationManager
) {

    val playing: LiveData<Boolean> get() = _playing
    private val _playing = MutableLiveData<Boolean>(false)

    val currentSong: LiveData<Song> get() = _currentSong
    private val _currentSong = MutableLiveData<Song>()
    private var mediaPlayer: MediaPlayer = MediaPlayer()

    init {
        playlistHandler.songs.observeForever {
            if (isPlaying()) return@observeForever
            if (it == null) return@observeForever
            val currentSong = playlistHandler.currentSong()
            if (currentSong != null) {
                initSong(currentSong)
            } else {
                playlistHandler.nextSong()?.let { song -> initSong(song) }
            }
        }

        repository.getRepeatState().observeForever {
            if (it == null) return@observeForever
            mediaPlayer.setOnCompletionListener { _ ->
                when (it) {
                    Repeat.NO_REPEAT -> {
                        mediaPlayer.seekTo(0)
                        pause()
                    }
                    Repeat.REPEAT_ALL -> {
                        val song = playlistHandler.nextSong()
                        song?.let { s ->
                            initSong(s)
                            start()
                        }
                    }
                    Repeat.REPEAT_ONE -> {
                        start()
                    }
                }
            }
        }
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
    fun queueSong() = playlistHandler.queueSong()
}

