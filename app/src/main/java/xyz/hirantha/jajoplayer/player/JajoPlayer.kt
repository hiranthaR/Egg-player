package xyz.hirantha.jajoplayer.player

import android.content.Context
import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import xyz.hirantha.jajoplayer.models.Song

class JajoPlayer(private val context: Context, private val playlistHandler: PlaylistHandler) :
    MediaPlayer.OnCompletionListener {

    val playing: LiveData<Boolean> get() = _playing
    private val _playing = MutableLiveData<Boolean>(false)

    val currentSong: LiveData<Song> get() = _currentSong
    private val _currentSong = MutableLiveData<Song>()

    private var mediaPlayer: MediaPlayer = MediaPlayer().apply {
        setOnCompletionListener(this@JajoPlayer)
    }

    fun playSong(song: Song) {
        playlistHandler.bringSongToFront(song)
        startSong(song)
        _playing.postValue(true)
    }

    fun playNextSong() {
        val nextSong = playlistHandler.nextSong()
        nextSong?.let { startSong(it) }
    }


    fun playPreviousSong() {
        val previousSong = playlistHandler.previousSong()
        previousSong?.let { startSong(it) }
    }


    private fun startSong(song: Song) {
        _currentSong.postValue(song)
        mediaPlayer.reset()
        mediaPlayer.setDataSource(context, song.getUri())
        mediaPlayer.prepare()
        mediaPlayer.start()
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
        song?.let { startSong(it) }
    }
}

