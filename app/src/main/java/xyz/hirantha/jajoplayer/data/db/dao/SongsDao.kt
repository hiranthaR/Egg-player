package xyz.hirantha.jajoplayer.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import xyz.hirantha.jajoplayer.models.Song

@Dao
interface SongsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertSongs(songs: List<Song>)

    @Query("SELECT * FROM songs;")
    fun getSongs(): LiveData<List<Song>>

    @Query("DELETE FROM songs;")
    fun deleteAll()
}