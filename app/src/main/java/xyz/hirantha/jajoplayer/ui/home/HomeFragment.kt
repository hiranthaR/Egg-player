package xyz.hirantha.jajoplayer.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.home_fragment.*
import kotlinx.coroutines.launch
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

import xyz.hirantha.jajoplayer.R
import xyz.hirantha.jajoplayer.internal.ScopedFragment
import xyz.hirantha.jajoplayer.models.Song
import xyz.hirantha.jajoplayer.ui.listitems.SongListItem

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
            initRecyclerView(it.toSongItems())
        })
    }

    private fun List<Song>.toSongItems(): List<SongListItem> {
        return this.map {
            SongListItem(context!!, it)
        }
    }

    private fun initRecyclerView(items: List<SongListItem>) {
        val groupAdapter = GroupAdapter<GroupieViewHolder>().apply {
            addAll(items)
            setOnItemClickListener { item, _ ->
                (item as? SongListItem)?.let {

                }
            }
        }
        rv_songs.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = groupAdapter
        }
    }
}
