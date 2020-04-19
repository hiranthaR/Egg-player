package xyz.hirantha.jajoplayer.data.providers

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

abstract class PreferenceProvider(context: Context) {
    // shared preference for cache data
    private val appContext = context.applicationContext
    protected val preference: SharedPreferences
        get() = PreferenceManager.getDefaultSharedPreferences(
            appContext
        )
}