package xyz.hirantha.jajoplayer.data.providers

import xyz.hirantha.jajoplayer.models.Song

interface PlayerStateProvider {

    fun setLastPlayedSong(song: Song)
    fun getLastPlayedSong(): Song?
}