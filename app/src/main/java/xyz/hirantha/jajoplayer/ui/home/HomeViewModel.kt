package xyz.hirantha.jajoplayer.ui.home

import androidx.lifecycle.ViewModel
import xyz.hirantha.jajoplayer.data.repository.MediaRepository

class HomeViewModel(private val mediaRepository: MediaRepository) : ViewModel() {
    fun getSongs() = mediaRepository.getSongs()
}
