package xyz.hirantha.jajoplayer.data.repository

import android.content.Context
import android.provider.MediaStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import xyz.hirantha.jajoplayer.data.db.dao.SongsDao
import xyz.hirantha.jajoplayer.models.Song

class MediaRepositoryImpl(
    private val context: Context,
    private val songsDao: SongsDao
) : MediaRepository {

    private val songProjection = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.ARTIST_ID,
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.ALBUM_ID,
        MediaStore.Audio.Media.ALBUM,
        MediaStore.Audio.Media.DURATION,
        MediaStore.Audio.Media.YEAR,
        MediaStore.Audio.Media.DATE_ADDED,
        MediaStore.Audio.Media.DATE_MODIFIED,
        MediaStore.Audio.Media.TRACK
    )

    private val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"

    override suspend fun getSongs(): LiveData<List<Song>> {
        return withContext(Dispatchers.IO) {
            val cursor = context.contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                songProjection,
                selection,
                null,
                MediaStore.Audio.Media.TITLE
            )
            val songs = mutableListOf<Song>()
            cursor?.let {
                while (cursor.moveToNext()) {
                    songs.add(Song(cursor))
                }
            }
            persistSongs(songs)
            return@withContext songsDao.getSongs()
        }
    }

    private fun persistSongs(songs: List<Song>) {
        GlobalScope.launch(Dispatchers.IO) {
            songsDao.deleteAll()
            songsDao.upsertSongs(songs)
        }
    }
}