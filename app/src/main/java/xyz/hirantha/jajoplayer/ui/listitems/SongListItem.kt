package xyz.hirantha.jajoplayer.ui.listitems

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import com.bumptech.glide.Glide
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.row_song.*
import org.threeten.bp.Duration
import xyz.hirantha.jajoplayer.R
import xyz.hirantha.jajoplayer.models.Song

class SongListItem(private val context: Context, val song: Song) : Item() {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val sArtworkUri = Uri
            .parse("content://media/external/audio/albumart");

        viewHolder.apply {
            Glide.with(context)
                .load(ContentUris.withAppendedId(sArtworkUri, song.albumId))
                .centerCrop()
                .placeholder(R.drawable.song)
                .error(R.drawable.song)
                .into(img_thumbnail)

            tv_name.text = song.title
            tv_artist.text = song.artistName
            val duration = Duration.ofMillis(song.duration)
            tv_duration.setText("${duration.toMinutes()}:${duration.seconds % 60}")
        }
    }

    override fun getLayout() = R.layout.row_song
}