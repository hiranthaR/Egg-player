package xyz.hirantha.jajoplayer.notification

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import xyz.hirantha.jajoplayer.R
import xyz.hirantha.jajoplayer.internal.getAlbumCoverUri
import xyz.hirantha.jajoplayer.models.Song
import xyz.hirantha.jajoplayer.services.NotificationActionService

const val CHANNEL_ID = "xyz.hirantha.jajoplayer.notification"

const val ACTION_PLAY = "xyz.hirantha.jajoplayer.action.play"
const val ACTION_PAUSE = "xyz.hirantha.jajoplayer.action.pause"
const val ACTION_NEXT = "xyz.hirantha.jajoplayer.action.next"
const val ACTION_PREVIOUS = "xyz.hirantha.jajoplayer.action.previous"

class NotificationManager(private val context: Context) {

    private var song: Song? = null

    fun initSong(song: Song) {
        this.song = song
    }

    fun getNotification(playButton: Boolean): Notification {

        val mediaSessionCompat = MediaSessionCompat(context, "jajoPlayer")

        val icon = getIcon(song?.getAlbumCoverUri())

        val btnPlay = if (playButton) R.drawable.ic_play_arrow else R.drawable.ic_pause
        val btnNext = R.drawable.ic_skip_next
        val btnPrevious = R.drawable.ic_skip_previous

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_music_note)
            .setContentTitle(song?.title)
            .setContentText(song?.artistName)
            .setLargeIcon(icon)
            .setOnlyAlertOnce(true)
            .setShowWhen(false)
            .setOngoing(true)
            .addAction(btnPrevious, "Previous", getPendingIntent(ACTION_PREVIOUS))
            .addAction(
                btnPlay,
                "Play",
                getPendingIntent(if (playButton) ACTION_PLAY else ACTION_PAUSE)
            )
            .addAction(btnNext, "Next", getPendingIntent(ACTION_NEXT))
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(0, 1, 2)
                    .setMediaSession(mediaSessionCompat.sessionToken)
            )
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun getPendingIntent(action: String): PendingIntent? {
        val intent = Intent(context, NotificationActionService::class.java)
        intent.action = action
        return PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun getIcon(uri: Uri?) = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)

    fun sendNotification(playButton: Boolean) {
        val notificationManagerCompat = NotificationManagerCompat.from(context)
        notificationManagerCompat.notify(1, getNotification(playButton))
    }
}