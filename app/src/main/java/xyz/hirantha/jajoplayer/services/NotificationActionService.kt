package xyz.hirantha.jajoplayer.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

const val RECEIVER_INTENT = "xyz.hirantha.jajoplayer.tracks"
const val RECEIVER_INTENT_ACTION_NAME = "xyz.hirantha.jajoplayer.action_name"

class NotificationActionService: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.sendBroadcast(
            Intent(RECEIVER_INTENT)
                .putExtra(RECEIVER_INTENT_ACTION_NAME,intent?.action)
        )
    }
}