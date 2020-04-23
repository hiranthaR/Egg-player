package xyz.hirantha.jajoplayer.data.repository

import androidx.lifecycle.LiveData
import xyz.hirantha.jajoplayer.internal.Repeat
import xyz.hirantha.jajoplayer.models.Song

interface Repository {
    fun setLastPlayedSong(song: Song)
    fun getLastPlayedSong(): Song?
    fun toggleRepeatState()
    fun getRepeatState(): LiveData<Repeat>
}