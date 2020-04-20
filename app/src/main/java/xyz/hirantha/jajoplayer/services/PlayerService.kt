package xyz.hirantha.jajoplayer.services

import android.app.NotificationChannel
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.util.Log
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance
import xyz.hirantha.jajoplayer.notification.*
import xyz.hirantha.jajoplayer.player.JajoPlayer

class PlayerService : Service(), KodeinAware {

    override val kodein: Kodein by closestKodein()
    private val player: JajoPlayer by instance()
    private val notificationManager: NotificationManager by instance()

    private var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            handleReceive(intent)
        }
    }

    companion object {
        var isRunning: Boolean = false
    }

    override fun onCreate() {
        super.onCreate()
        Log.e("playerService", "onCreate")
        isRunning = true
        initNotificationChannel()
        startForeground(1, notificationManager.getNotification(false))
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        registerReceiver(broadcastReceiver, IntentFilter(RECEIVER_INTENT))
        return super.onStartCommand(intent, flags, startId)
    }

    private fun handleReceive(intent: Intent?) {
        when (intent?.extras?.getString(RECEIVER_INTENT_ACTION_NAME)) {
            ACTION_PLAY -> {
                player.start()
            }
            ACTION_PAUSE -> {
                player.pause()
            }
            ACTION_NEXT -> {
                player.playNextSong()
            }
            ACTION_PREVIOUS -> player.playPreviousSong()
        }
    }

    private fun initNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "jajoPlayer",
                android.app.NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(android.app.NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        unregisterReceiver(broadcastReceiver)
        isRunning = false
        Log.e("playerService", "Destroyed")
        super.onDestroy()
    }
}