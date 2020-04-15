package xyz.hirantha.jajoplayer.data.repository

import android.content.Context
import android.provider.MediaStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import xyz.hirantha.jajoplayer.models.Song

class MediaRepositoryImpl(
    private val context: Context
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

    override fun getSongs(): LiveData<List<Song>> {
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
        return MutableLiveData<List<Song>>(songs)
    }
}