package xyz.hirantha.jajoplayer.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import xyz.hirantha.jajoplayer.data.repository.MediaRepository
import xyz.hirantha.jajoplayer.player.PlaylistHandler

class HomeViewModelFactory(
    private val mediaRepository: MediaRepository,
    private val playlistHandler: PlaylistHandler
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return HomeViewModel(mediaRepository, playlistHandler) as T
    }
}