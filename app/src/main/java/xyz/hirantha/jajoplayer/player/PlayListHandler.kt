package xyz.hirantha.jajoplayer.player

import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import xyz.hirantha.jajoplayer.models.Song

interface PlayListHandler : MediaPlayer.OnCompletionListener{
    val currentSong: LiveData<Song>

    fun onDestroy()
    fun playSong(song: Song,mediaPlayer: MediaPlayer)
}