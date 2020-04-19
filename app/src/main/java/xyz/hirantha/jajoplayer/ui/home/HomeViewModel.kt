package xyz.hirantha.jajoplayer.ui.home

import androidx.lifecycle.ViewModel
import xyz.hirantha.jajoplayer.data.repository.MediaRepository
import xyz.hirantha.jajoplayer.internal.lazyDeferred
import xyz.hirantha.jajoplayer.player.PlaylistHandler

class HomeViewModel(
    private val mediaRepository: MediaRepository,
    private val playlistHandler: PlaylistHandler
) : ViewModel() {
    val songs by lazyDeferred { mediaRepository.getSongs() }
}
