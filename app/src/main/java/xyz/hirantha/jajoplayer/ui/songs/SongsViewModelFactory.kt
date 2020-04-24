package xyz.hirantha.jajoplayer.ui.songs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SongsViewModelFactory : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SongsViewModel() as T
    }
}