package xyz.hirantha.jajoplayer.data.repository

import xyz.hirantha.jajoplayer.models.Song

interface Repository {
    fun setLastPlayedSong(song: Song)
    fun getLastPlayedSong(): Song?
}