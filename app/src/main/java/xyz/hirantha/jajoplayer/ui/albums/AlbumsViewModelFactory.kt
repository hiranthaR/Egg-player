package xyz.hirantha.jajoplayer.ui.albums

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AlbumsViewModelFactory : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AlbumsViewModel() as T
    }
}