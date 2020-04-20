package xyz.hirantha.jajoplayer.data.providers

import android.content.Context
import com.google.gson.Gson
import xyz.hirantha.jajoplayer.models.Song

const val LAST_PLAYED_SONG = "xyz.hirantha.jajoplayer.pref.last_played_song"

class PlayerStateProviderImpl(context: Context) : PreferenceProvider(context), PlayerStateProvider {

    override fun setLastPlayedSong(song: Song) {
        preference.edit().putString(LAST_PLAYED_SONG, Gson().toJson(song)).apply()
    }

    override fun getLastPlayedSong(): Song? =
        Gson().fromJson(preference.getString(LAST_PLAYED_SONG, "{}"), Song::class.java)
}