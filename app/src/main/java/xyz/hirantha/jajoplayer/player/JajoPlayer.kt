package xyz.hirantha.jajoplayer.player

import android.content.Context
import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import xyz.hirantha.jajoplayer.data.repository.Repository
import xyz.hirantha.jajoplayer.internal.getUri
import xyz.hirantha.jajoplayer.models.Song

class JajoPlayer(
    private val context: Context,
    private val playlistHandler: PlaylistHandler,
    private val repository: Repository
) :
    MediaPlayer.OnCompletionListener {

    val playing: LiveData<Boolean> get() = _playing
    private val _playing = MutableLiveData<Boolean>(false)

    val currentSong: LiveData<Song> get() = _currentSong
    private val _currentSong = MutableLiveData<Song>().also {
        val lastPlayedSong = repository.getLastPlayedSong()
        if (lastPlayedSong != null) {
            if (playlistHandler.bringSongToFront(lastPlayedSong)) {
                initSong(lastPlayedSong)
            } else {
                playlistHandler.nextSong()?.let { initSong(it) }
            }
        } else {
            playlistHandler.nextSong()?.let { initSong(it) }
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
        repository.setLastPlayedSong(song)
        _currentSong.postValue(song)
        mediaPlayer.reset()
        mediaPlayer.setDataSource(context, song.getUri())
        mediaPlayer.prepare()
    }

    fun start() {
        mediaPlayer.start()
        _playing.postValue(true)
    }

    fun stop() {
        mediaPlayer.stop()
        _playing.postValue(false)
    }

    fun pause() {
        mediaPlayer.pause()
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

