package xyz.hirantha.jajoplayer.data.repository

interface Repository {
    fun setLastPlayedSong(id: Long)
    fun getLastPlayedSong(): Long
}