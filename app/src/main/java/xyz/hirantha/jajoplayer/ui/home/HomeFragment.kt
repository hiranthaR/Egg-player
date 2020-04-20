package xyz.hirantha.jajoplayer.ui.home

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.home_fragment.*
import kotlinx.android.synthetic.main.player_bottom_sheet.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance
import org.threeten.bp.Duration
import xyz.hirantha.jajoplayer.R
import xyz.hirantha.jajoplayer.internal.ScopedFragment
import xyz.hirantha.jajoplayer.internal.getAlbumCoverUri
import xyz.hirantha.jajoplayer.internal.toMMSS
import xyz.hirantha.jajoplayer.models.Song
import xyz.hirantha.jajoplayer.notification.CreateNotification
import xyz.hirantha.jajoplayer.player.JajoPlayer
import xyz.hirantha.jajoplayer.services.OnClearFromRecentService
import xyz.hirantha.jajoplayer.services.RECEIVER_INTENT
import xyz.hirantha.jajoplayer.ui.listitems.SongListItem

class HomeFragment : ScopedFragment(), KodeinAware {

    override val kodein: Kodein by closestKodein()
    private lateinit var viewModel: HomeViewModel
    private val viewModelFactory: HomeViewModelFactory by instance()
    private var notificationManager: NotificationManager? = null
    private var position = 0
    private var isPlaying = false
    private var broadcastReceiver: BroadcastReceiver
    private var songs: List<Song>? = null

    private val jajoPlayer: JajoPlayer by instance()

    init {
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
//                when (intent?.extras?.getString(RECEIVER_INTENT_ACTION_NAME)) {
//                    CreateNotification.ACTION_PREVIOUS -> {
//                        onTrackPrevious()
//                    }
//                    CreateNotification.ACTION_PLAY -> {
//                        onTrackPlay()
//                    }
//                    CreateNotification.ACTION_PAUSE -> {
//                        onTrackPause()
//                    }
//                    CreateNotification.ACTION_NEXT -> {
//                        onTrackNext()
//                    }
//                }
            }

        }
    }

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
        bindPlayerBottomSheet()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
            activity?.registerReceiver(broadcastReceiver, IntentFilter(RECEIVER_INTENT))
            activity?.startService(Intent(context!!, OnClearFromRecentService::class.java))
        }

    }

    private fun bindPlayerBottomSheet() {
        val bottomSheetBehavior = BottomSheetBehavior.from(player_bottom_sheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                title_panel_expanded.animate().alpha(slideOffset)
                title_panel_collapsed.animate().alpha(1 - slideOffset)
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    title_panel_collapsed.visibility = View.GONE
                    title_panel_expanded.visibility = View.VISIBLE
                }
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    title_panel_collapsed.visibility = View.VISIBLE
                    title_panel_expanded.visibility = View.GONE
                }
            }
        })

        jajoPlayer.currentSong.observe(viewLifecycleOwner, Observer {
            if (it == null) return@Observer
            Glide.with(context!!)
                .load(it.getAlbumCoverUri())
                .centerCrop()
                .placeholder(R.drawable.song)
                .error(R.drawable.song)
                .into(img_song_cover)
            tv_song_title.text = it.title

            val duration = Duration.ofMillis(jajoPlayer.getDuration().toLong())
            tv_song_duration.text = duration.toMMSS()
        })

        GlobalScope.launch(Dispatchers.Main) {
            while (true) {
                if (jajoPlayer.isPlaying()) {
                    val duration = Duration.ofMillis(jajoPlayer.getCurrentPosition().toLong())
                    tv_current_position.text = duration.toMMSS()
                }
                delay(1000)
            }
        }

        jajoPlayer.playing.observe(viewLifecycleOwner, Observer {
            if (it == null) return@Observer
            if (it) {
                btn_play_pause.setImageResource(R.drawable.ic_pause)
            } else {
                btn_play_pause.setImageResource(R.drawable.ic_play_arrow)
            }
        })

        btn_play_pause.setOnClickListener {
            if (jajoPlayer.isPlaying()) {
                jajoPlayer.pause()
            } else {
                jajoPlayer.start()
            }
        }

        btn_next.setOnClickListener { jajoPlayer.playNextSong() }
        btn_previous.setOnClickListener { jajoPlayer.playPreviousSong() }
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CreateNotification.CANNEL_ID,
                "Jajo Player",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager = activity?.getSystemService(NotificationManager::class.java)
            notificationManager?.apply {
                createNotificationChannel(notificationChannel)
            }
        }
    }

    private fun bindUI() = launch {
        viewModel.songs.await().observe(viewLifecycleOwner, Observer {
            if (it == null) return@Observer
            initRecyclerView(it.toSongItems())
            songs = it
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
                    jajoPlayer.playSong(item.song)
                }
            }
        }
        rv_songs.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = groupAdapter
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager?.cancelAll()
        }
        activity?.unregisterReceiver(broadcastReceiver)
    }
}
