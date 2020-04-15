package xyz.hirantha.jajoplayer.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.launch
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

import xyz.hirantha.jajoplayer.R
import xyz.hirantha.jajoplayer.internal.ScopedFragment

class HomeFragment : ScopedFragment(), KodeinAware {

    override val kodein: Kodein by closestKodein()
    private lateinit var viewModel: HomeViewModel
    private val viewModelFactory: HomeViewModelFactory by instance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory).get(HomeViewModel::class.java)
        bindUI()
    }

    private fun bindUI() = launch {
        viewModel.songs.await().observe(viewLifecycleOwner, Observer {
            if (it == null) return@Observer
            Log.e("songs", it.toString())
        })
    }
}
