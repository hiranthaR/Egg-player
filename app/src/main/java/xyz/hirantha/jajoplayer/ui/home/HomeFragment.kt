package xyz.hirantha.jajoplayer.ui.home

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.res.ColorStateList
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.home_fragment.*
import kotlinx.android.synthetic.main.player_bottom_sheet.*
import kotlinx.coroutines.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance
import org.threeten.bp.Duration
import xyz.hirantha.jajoplayer.R
import xyz.hirantha.jajoplayer.internal.Repeat
import xyz.hirantha.jajoplayer.internal.ScopedFragment
import xyz.hirantha.jajoplayer.internal.getAlbumCoverUri
import xyz.hirantha.jajoplayer.internal.toMMSS
import xyz.hirantha.jajoplayer.models.Song
import xyz.hirantha.jajoplayer.notification.CHANNEL_ID
import xyz.hirantha.jajoplayer.player.JajoPlayer
import xyz.hirantha.jajoplayer.services.PlayerService
import xyz.hirantha.jajoplayer.ui.listitems.SongListItem

class HomeFragment : ScopedFragment(), KodeinAware {

    override val kodein: Kodein by closestKodein()
    private lateinit var viewModel: HomeViewModel
    private val viewModelFactory: HomeViewModelFactory by instance()
    private val jajoPlayer: JajoPlayer by instance()

    private var manager: NotificationManager? = null
    private var durationJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory).get(HomeViewModel::class.java)
        checkPermissions()
        initNotificationChannel()
    }

    private fun initNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "jajoPlayer",
                NotificationManager.IMPORTANCE_LOW
            )
            manager = activity?.getSystemService(android.app.NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    private fun checkPermissions() {
        Dexter.withActivity(activity).withPermissions(
            listOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.FOREGROUND_SERVICE
            )
        ).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                if (report != null && report.areAllPermissionsGranted()) {
                    bindUI()
                    bindPlayerBottomSheet()
                }
            }

            override fun onPermissionRationaleShouldBeShown(
                permissions: MutableList<PermissionRequest>?,
                token: PermissionToken?
            ) {

            }
        }).check()
    }

    private fun bindPlayerBottomSheet() {
        val bottomSheetBehavior = BottomSheetBehavior.from(player_bottom_sheet)

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

            Glide.with(context!!)
                .load(it.getAlbumCoverUri())
                .centerCrop()
                .placeholder(R.drawable.song)
                .error(R.drawable.song)
                .into(img_song_cover_expanded)

            tv_song_title.text = it.title
            tv_song_title_expanded.text = it.title

            val duration = Duration.ofMillis(jajoPlayer.getDuration().toLong())
            tv_song_duration.text = duration.toMMSS()
            tv_song_duration_expanded.text = duration.toMMSS()
            sb_song_progress.progress = 0
            sb_song_progress.max = jajoPlayer.getDuration()

            tv_artist_expanded.text = it.artistName
            tv_album_name_expanded.text = it.albumName

            tv_next_song.text = jajoPlayer.queueSong()?.title

            btn_favorite.setImageResource(if (it.favorite) R.drawable.ic_favorite else R.drawable.ic_favorite_border)
            var favoriteTemp = it.favorite
            btn_favorite.setOnClickListener { _ ->
                viewModel.toggleFavorite(it)
                favoriteTemp = !favoriteTemp
                btn_favorite.setImageResource(if (favoriteTemp) R.drawable.ic_favorite else R.drawable.ic_favorite_border)
            }
        })

        sb_song_progress.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    jajoPlayer.seekTo(progress)
                    sb_song_progress.progress = progress
                    val duration = Duration.ofMillis(progress.toLong())
                    tv_current_position.text = duration.toMMSS()
                    tv_current_position_expanded.text = duration.toMMSS()
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })

        durationJob = GlobalScope.launch(Dispatchers.Main) {
            while (true) {
                if (jajoPlayer.isPlaying()) {
                    val duration = Duration.ofMillis(jajoPlayer.getCurrentPosition().toLong())
                    tv_current_position.text = duration.toMMSS()
                    tv_current_position_expanded.text = duration.toMMSS()
                    sb_song_progress.progress = jajoPlayer.getCurrentPosition()
                }
                delay(1000)
            }
        }

        jajoPlayer.playing.observe(viewLifecycleOwner, Observer {
            if (it == null) return@Observer
            if (it) {
                btn_play_pause.setImageResource(R.drawable.ic_pause)
                btn_play_pause_expanded.setImageResource(R.drawable.ic_pause)
            } else {
                btn_play_pause.setImageResource(R.drawable.ic_play_arrow)
                btn_play_pause_expanded.setImageResource(R.drawable.ic_play_arrow)
            }
        })

        viewModel.repeatState.observe(viewLifecycleOwner, Observer {
            if (it == null) return@Observer
            when (it) {
                Repeat.NO_REPEAT -> {
                    btn_repeat_expanded.setColorFilter(
                        ContextCompat.getColor(context!!, android.R.color.darker_gray)

                    )
                    btn_repeat_expanded.setImageDrawable(context!!.getDrawable(R.drawable.ic_repeat))
                }
                Repeat.REPEAT_ALL -> {
                    btn_repeat_expanded.setColorFilter(
                        ContextCompat.getColor(context!!, android.R.color.white)
                    )
                    btn_repeat_expanded.setImageDrawable(context!!.getDrawable(R.drawable.ic_repeat))
                }
                Repeat.REPEAT_ONE -> {
                    btn_repeat_expanded.setColorFilter(
                        ContextCompat.getColor(context!!, android.R.color.white)
                    )
                    btn_repeat_expanded.setImageDrawable(context!!.getDrawable(R.drawable.ic_repeat_one))
                }
            }
        })

        btn_play_pause.setOnClickListener { actionBtnPlay() }
        btn_play_pause_expanded.setOnClickListener { actionBtnPlay() }
        btn_next.setOnClickListener { actionBtnNext() }
        btn_next_expanded.setOnClickListener { actionBtnNext() }
        btn_previous.setOnClickListener { actionBtnPrevious() }
        btn_previous_expanded.setOnClickListener { actionBtnPrevious() }
        btn_repeat_expanded.setOnClickListener { viewModel.toggleRepeatState() }
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
                    jajoPlayer.playSong(item.song)
                    if (!PlayerService.isRunning) {
                        startService()
                    }
                }
            }
        }
        rv_songs.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = groupAdapter
        }
    }

    override fun onDestroy() {
        durationJob?.cancel()
        super.onDestroy()
    }

    private fun startService() {
        ContextCompat.startForegroundService(
            context!!,
            Intent(
                context,
                PlayerService::class.java
            )
        )
    }

    private fun stopService() {
        context?.stopService(
            Intent(
                context,
                PlayerService::class.java
            )
        )
    }

    private fun actionBtnPlay() {
        if (jajoPlayer.isPlaying()) {
            jajoPlayer.pause()
            manager?.cancelAll()
            if (PlayerService.isRunning) {
                stopService()
            }
        } else {
            jajoPlayer.start()
            if (!PlayerService.isRunning) {
                startService()
            }
        }
    }

    private fun actionBtnNext() {
        jajoPlayer.playNextSong()
        if (!PlayerService.isRunning) {
            startService()
        }
    }

    private fun actionBtnPrevious() {
        jajoPlayer.playPreviousSong()
        if (!PlayerService.isRunning) {
            startService()
        }
    }
}
