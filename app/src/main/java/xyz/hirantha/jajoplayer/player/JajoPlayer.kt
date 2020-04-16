package xyz.hirantha.jajoplayer.player

import android.content.Context
import android.media.MediaPlayer
import xyz.hirantha.jajoplayer.models.Song

class JajoPlayer(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    private var currentSong: Song? = null

    fun initSong(song: Song) {
        if (currentSong != song) {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer.create(context, song.getUri())
            } else {
                if (isPlaying()!!) {
                    playNew(song)
                }
            }
        }
    }

    fun start() = mediaPlayer?.start()
    fun stop() = mediaPlayer?.stop()
    fun pause() = mediaPlayer?.pause()
    fun isPlaying() = mediaPlayer?.isPlaying
    fun getDuration() = mediaPlayer?.duration
    fun getCurrentPosition() = mediaPlayer?.currentPosition
    fun seekTo(position: Int) = mediaPlayer?.seekTo(position)

    private fun playNew(song: Song) {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(context, song.getUri())
    }
}