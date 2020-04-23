package xyz.hirantha.jajoplayer.data.repository

import androidx.lifecycle.LiveData
import xyz.hirantha.jajoplayer.data.providers.PlayerStateProvider
import xyz.hirantha.jajoplayer.internal.Repeat
import xyz.hirantha.jajoplayer.models.Song

class RepositoryImpl(
    private val playerStateProvider: PlayerStateProvider
) : Repository {
    override fun setLastPlayedSong(song: Song) = playerStateProvider.setLastPlayedSong(song)

    override fun getLastPlayedSong() = playerStateProvider.getLastPlayedSong()

    override fun toggleRepeatState() =
        playerStateProvider.toggleRepeatState()

    override fun getRepeatState(): LiveData<Repeat> = playerStateProvider.repeatState
}