package xyz.hirantha.jajoplayer.models

import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "songs")
data class Song(
    @PrimaryKey(autoGenerate = false)
    val id: Long,
    val title: String,
    val albumId: Long,
    val albumName: String,
    val artistId: Long,
    val artistName: String,
    val duration: Long,
    val year: Int,
    val dateAdded: Int,
    val dateModified: Long,
    var track: Int,
    var favorite: Boolean = false
) {

    constructor(cursor: Cursor) : this(
        cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)),
        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)),
        cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)),
        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)),
        cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST_ID)),
        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)),
        cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)),
        cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR)),
        cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)),
        cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_MODIFIED)),
        cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK))
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false
        other as Song
        return other.id == id && other.title == title
    }
}
