package xyz.hirantha.jajoplayer.player

import androidx.lifecycle.LiveData
import xyz.hirantha.jajoplayer.models.Song
import java.util.*

interface PlaylistHandler {
    val songs: LiveData<List<Song>>
    fun bringSongToFront(song: Song): Boolean
    fun nextSong(): Song?
    fun currentSong(): Song?
    fun previousSong(): Song?
}