package xyz.hirantha.jajoplayer.player

import xyz.hirantha.jajoplayer.models.Song
import java.util.*

interface PlaylistHandler {
    val queue: Deque<Song>
    fun bringSongToFront(song: Song): Boolean
    fun nextSong(): Song?
    fun previousSong(): Song?
}