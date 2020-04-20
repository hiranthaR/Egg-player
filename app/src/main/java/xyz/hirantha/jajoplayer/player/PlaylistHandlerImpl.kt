package xyz.hirantha.jajoplayer.player

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import xyz.hirantha.jajoplayer.data.repository.MediaRepository
import xyz.hirantha.jajoplayer.data.repository.Repository
import xyz.hirantha.jajoplayer.internal.lazyDeferred
import xyz.hirantha.jajoplayer.models.Song
import java.util.*
import kotlin.coroutines.CoroutineContext

class PlaylistHandlerImpl(
    context: Context,
    private val mediaRepository: MediaRepository,
    private val repository: Repository
) : KodeinAware, CoroutineScope, PlaylistHandler {

    private var job: Job = Job()
    override val coroutineContext: CoroutineContext get() = job + Dispatchers.Main
    override val kodein: Kodein by closestKodein(context)

    private val songsList by lazyDeferred { mediaRepository.getSongs() }
    private val queue: Deque<Song> = LinkedList<Song>()

    override val songs: LiveData<List<Song>> get() = _songs
    private val _songs = MutableLiveData<List<Song>>()

    init {
        observeSongs()
    }

    private fun observeSongs() = launch {
        songsList.await().observeForever {
            if (it == null) return@observeForever
            queue.clear()
            queue.addAll(it)
            _songs.postValue(it)
        }
    }

    override fun bringSongToFront(song: Song): Boolean {
        var index = 0
        while (queue.peekFirst() != song && index < queue.size) {
            index++
            queue.addLast(queue.pollFirst())
        }
        if (index >= queue.size) return false
        queue.addLast(queue.pollFirst())
        return true
    }

    override fun nextSong(): Song? {
        val song = queue.pollFirst()
        queue.addLast(song)
        return song
    }

    override fun previousSong(): Song? {
        val song = queue.pollLast()
        queue.addFirst(song)
        return queue.peekLast()
    }
}