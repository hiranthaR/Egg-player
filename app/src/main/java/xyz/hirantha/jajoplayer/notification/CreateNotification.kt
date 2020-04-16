package xyz.hirantha.jajoplayer.notification

import android.app.Notification
import android.app.PendingIntent
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import xyz.hirantha.jajoplayer.R
import xyz.hirantha.jajoplayer.models.Song
import xyz.hirantha.jajoplayer.services.NotificationActionService

object CreateNotification {

    lateinit var notification: Notification

    val CANNEL_ID = "xyz.hirantha.jajoplayer.notification"

    val ACTION_PLAY = "xyz.hirantha.jajoplayer.action.play"
    val ACTION_PAUSE = "xyz.hirantha.jajoplayer.action.pause"
    val ACTION_NEXT = "xyz.hirantha.jajoplayer.action.next"
    val ACTION_PREVIOUS = "xyz.hirantha.jajoplayer.action.previous"

    fun createNotification(context: Context, song: Song, playButton: Int, pos: Int, size: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManagerCompat = NotificationManagerCompat.from(context)
            val mediaSessionCompat = MediaSessionCompat(context, "tag")

            val sArtworkUri = Uri
                .parse("content://media/external/audio/albumart");


            val icon = MediaStore.Images.Media.getBitmap(
                context.contentResolver,
                ContentUris.withAppendedId(sArtworkUri, song.albumId)
            )


            val intentPrevious = Intent(context, NotificationActionService::class.java).apply {
                action = ACTION_PREVIOUS
            }

            val pendingIntentPrevious = PendingIntent.getBroadcast(
                context,
                0,
                intentPrevious,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            val drwPrevious = R.drawable.ic_skip_previous


            val intentPlay = Intent(context, NotificationActionService::class.java).apply {
                action = ACTION_PLAY
            }
            val pendingIntentPlay = PendingIntent.getBroadcast(
                context,
                0,
                intentPlay,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

            val intentNext = Intent(context, NotificationActionService::class.java).apply {
                action = ACTION_NEXT
            }
            val pendingIntentNext = PendingIntent.getBroadcast(
                context,
                0,
                intentNext,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            val drwNext = R.drawable.ic_skip_next


            notification = NotificationCompat.Builder(context, CANNEL_ID)
                .setSmallIcon(R.drawable.ic_music_note)
                .setContentTitle(song.title)
                .setContentText(song.artistName)
                .setLargeIcon(icon)
                .setOnlyAlertOnce(true)
                .setShowWhen(false)
                .setOngoing(true)
                .addAction(drwPrevious, "Previous", pendingIntentPrevious)
                .addAction(playButton, "Play", pendingIntentPlay)
                .addAction(drwNext, "Next", pendingIntentNext)
                .setStyle(
                    androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0, 1, 2)
                        .setMediaSession(mediaSessionCompat.sessionToken)
                )
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build()

            notificationManagerCompat.notify(1, notification)
        }
    }
}