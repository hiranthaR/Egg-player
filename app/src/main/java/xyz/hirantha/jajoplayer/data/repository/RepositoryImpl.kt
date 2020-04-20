package xyz.hirantha.jajoplayer.data.repository

import xyz.hirantha.jajoplayer.data.providers.PlayerStateProvider
import xyz.hirantha.jajoplayer.models.Song

class RepositoryImpl(
    private val playerStateProvider: PlayerStateProvider
) : Repository {
    override fun setLastPlayedSong(song:Song) = playerStateProvider.setLastPlayedSong(song)

    override fun getLastPlayedSong() = playerStateProvider.getLastPlayedSong()
}