package xyz.hirantha.jajoplayer.data.providers

import androidx.lifecycle.LiveData
import xyz.hirantha.jajoplayer.internal.Repeat
import xyz.hirantha.jajoplayer.models.Song

interface PlayerStateProvider {
    val repeatState: LiveData<Repeat>
    fun setLastPlayedSong(song: Song)
    fun getLastPlayedSong(): Song?
    fun setRepeatState(repeatState: Repeat)
    fun getRepeatState(): Repeat
    fun toggleRepeatState()
}