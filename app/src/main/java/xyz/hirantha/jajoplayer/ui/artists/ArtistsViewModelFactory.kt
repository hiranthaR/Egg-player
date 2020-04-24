package xyz.hirantha.jajoplayer.ui.artists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ArtistsViewModelFactory:ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ArtistsViewModel() as T
    }
}