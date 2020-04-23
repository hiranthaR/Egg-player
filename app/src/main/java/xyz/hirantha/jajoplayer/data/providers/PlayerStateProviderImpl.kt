package xyz.hirantha.jajoplayer.data.providers

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import xyz.hirantha.jajoplayer.internal.Repeat
import xyz.hirantha.jajoplayer.models.Song

const val LAST_PLAYED_SONG = "xyz.hirantha.jajoplayer.pref.last_played_song"
const val REPEAT = "xyz.hirantha.jajoplayer.pref.repeat"

class PlayerStateProviderImpl(context: Context) : PreferenceProvider(context), PlayerStateProvider {
    override val repeatState: LiveData<Repeat> get() = _repeatState
    private val _repeatState = MutableLiveData<Repeat>(getRepeatState())

    override fun setLastPlayedSong(song: Song) {
        preference.edit().putString(LAST_PLAYED_SONG, Gson().toJson(song)).apply()
    }

    override fun getLastPlayedSong(): Song? =
        Gson().fromJson(preference.getString(LAST_PLAYED_SONG, "{}"), Song::class.java)

    override fun setRepeatState(repeatState: Repeat) {
        preference.edit().putString(REPEAT, repeatState.name).apply()
        _repeatState.postValue(repeatState)
    }

    override fun toggleRepeatState() {
        when (getRepeatState()) {
            Repeat.NO_REPEAT -> {
                setRepeatState(Repeat.REPEAT_ALL)
            }
            Repeat.REPEAT_ALL -> {
                setRepeatState(Repeat.REPEAT_ONE)
            }
            Repeat.REPEAT_ONE -> {
                setRepeatState(Repeat.NO_REPEAT)
            }
        }
    }

    override fun getRepeatState(): Repeat =
        Repeat.valueOf(preference.getString(REPEAT, Repeat.REPEAT_ALL.name)!!)
}