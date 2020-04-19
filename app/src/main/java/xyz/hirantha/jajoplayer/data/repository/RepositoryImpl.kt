package xyz.hirantha.jajoplayer.data.repository

import xyz.hirantha.jajoplayer.data.providers.PlayerStateProvider

class RepositoryImpl(
    private val playerStateProvider: PlayerStateProvider
) : Repository {
    override fun setLastPlayedSong(id: Long) = playerStateProvider.setLastPlayedSong(id)

    override fun getLastPlayedSong() = playerStateProvider.getLastPlayedSong()
}