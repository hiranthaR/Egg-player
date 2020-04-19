package xyz.hirantha.jajoplayer.data.providers

interface PlayerStateProvider {

    fun setLastPlayedSong(id: Long)
    fun getLastPlayedSong(): Long
}