package xyz.hirantha.jajoplayer.ui.home

import androidx.lifecycle.ViewModel
import xyz.hirantha.jajoplayer.data.repository.MediaRepository
import xyz.hirantha.jajoplayer.data.repository.Repository
import xyz.hirantha.jajoplayer.internal.Repeat
import xyz.hirantha.jajoplayer.internal.lazyDeferred
import xyz.hirantha.jajoplayer.player.PlaylistHandler

class HomeViewModel(
    private val mediaRepository: MediaRepository,
    private val repository: Repository
) : ViewModel() {
    val songs by lazyDeferred { mediaRepository.getSongs() }
    val repeatState = repository.getRepeatState()
    fun toggleRepeatState() = repository.toggleRepeatState()
}
