package xyz.hirantha.jajoplayer.data.providers

import android.content.Context

const val LAST_PLAYED_SONG = "xyz.hirantha.jajoplayer.pref.last_played_song"

class PlayerStateProviderImpl(context: Context) : PreferenceProvider(context), PlayerStateProvider {

    override fun setLastPlayedSong(id: Long) {
        preference.edit().putLong(LAST_PLAYED_SONG, id).apply()
    }

    override fun getLastPlayedSong(): Long = preference.getLong(LAST_PLAYED_SONG, -1)
}