package xyz.hirantha.jajoplayer.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import org.kodein.di.Kodein
import org.kodein.di.android.closestKodein

const val RECEIVER_INTENT = "xyz.hirantha.jajoplayer.tracks"
const val RECEIVER_INTENT_ACTION_NAME = "xyz.hirantha.jajoplayer.action_name"

class NotificationActionService: BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {

    }
}