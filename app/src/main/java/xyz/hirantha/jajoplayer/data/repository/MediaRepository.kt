package xyz.hirantha.jajoplayer.data.repository

import androidx.lifecycle.LiveData
import xyz.hirantha.jajoplayer.models.Song

interface MediaRepository {
    fun getSongs():LiveData<List<Song>>
}