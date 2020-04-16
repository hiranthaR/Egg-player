package xyz.hirantha.jajoplayer.player

import android.media.MediaPlayer
import xyz.hirantha.jajoplayer.models.Song

class JajoPlayer(private val playListHandler: PlayListHandler) {

    private var mediaPlayer: MediaPlayer = MediaPlayer().apply {
        setOnCompletionListener(playListHandler)
    }

    fun playSong(song: Song) {
        playListHandler.playSong(song, mediaPlayer)
    }

    fun currentSong() = playListHandler.currentSong
    fun start() = mediaPlayer.start()
    fun stop() = mediaPlayer.stop()
    fun pause() = mediaPlayer.pause()
    fun isPlaying() = mediaPlayer.isPlaying
    fun getDuration() = mediaPlayer.duration
    fun getCurrentPosition() = mediaPlayer.currentPosition
    fun seekTo(position: Int) = mediaPlayer.seekTo(position)

}