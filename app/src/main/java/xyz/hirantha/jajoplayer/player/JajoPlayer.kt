package xyz.hirantha.jajoplayer.player

import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import xyz.hirantha.jajoplayer.models.Song

class JajoPlayer(private val playListHandler: PlayListHandler) {

    val playing: LiveData<Boolean> get() = _playing
    private val _playing = MutableLiveData<Boolean>(false)

    private var mediaPlayer: MediaPlayer = MediaPlayer().apply {
        setOnCompletionListener(playListHandler)
    }

    fun playSong(song: Song) {
        playListHandler.playSong(song, mediaPlayer)
        _playing.postValue(true)
    }

    fun currentSong() = playListHandler.currentSong
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

}

