package xyz.hirantha.jajoplayer.player

import android.content.Context
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

    private val songs by lazyDeferred { mediaRepository.getSongs() }
    override val queue: Deque<Song> = LinkedList<Song>()

    init {
        observeSongs()
    }

    private fun observeSongs() = launch {
        songs.await().observeForever {
            if (it == null) return@observeForever
            queue.clear()
            queue.addAll(it)
        }
    }

    override fun bringSongToFront(song: Song) {
        while (queue.peekFirst() != song) {
            queue.addLast(queue.pollFirst())
        }
        queue.addLast(queue.pollFirst())
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