package xyz.hirantha.jajoplayer.internal

import android.content.ContentUris
import android.net.Uri
import android.provider.MediaStore
import org.threeten.bp.Duration
import xyz.hirantha.jajoplayer.models.Song

private val sArtworkUri = Uri
    .parse("content://media/external/audio/albumart");

fun Duration.toMMSS(): String {
    val formattedMinutes = String.format("%02d", this.toMinutes())
    val formattedSeconds = String.format("%02d", this.seconds % 60)
    return "${formattedMinutes}:${formattedSeconds}"
}

fun Song.getUri(): Uri =
    Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id.toString())

fun Song.getAlbumCoverUri(): Uri = ContentUris.withAppendedId(sArtworkUri, albumId)
