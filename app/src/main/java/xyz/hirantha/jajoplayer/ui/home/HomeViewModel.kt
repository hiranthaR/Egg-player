package xyz.hirantha.jajoplayer.ui.home

import androidx.lifecycle.ViewModel
import xyz.hirantha.jajoplayer.data.repository.MediaRepository
import xyz.hirantha.jajoplayer.internal.lazyDeferred

class HomeViewModel(private val mediaRepository: MediaRepository) : ViewModel() {
    val songs by lazyDeferred { mediaRepository.getSongs() }
}
