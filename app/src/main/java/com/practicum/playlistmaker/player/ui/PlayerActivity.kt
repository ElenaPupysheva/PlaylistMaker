package com.practicum.playlistmaker.player.ui

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.launchIn
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityPlayerBinding
import com.practicum.playlistmaker.domain.models.EXTRA_TRACK
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.media.domain.PlaylistInteractor
import com.practicum.playlistmaker.media.ui.NewPlaylistFragment
import com.practicum.playlistmaker.player.data.dto.PlayerState
import com.practicum.playlistmaker.player.data.service.MusicService
import com.practicum.playlistmaker.player.presentation.PlayerUiState
import com.practicum.playlistmaker.player.presentation.PlayerViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Locale

class PlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerBinding
    private val viewModel: PlayerViewModel by viewModel()
    private val playlistInteractor: PlaylistInteractor by inject()
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var bottomSheetAdapter: BottomSheetPlaylistsAdapter
    private lateinit var currentTrack: Track
    private var musicService: MusicService? = null
    private var isFinishingByUser = false

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            startMusicService()
        } else {
            Toast.makeText(this, "Разрешение на уведомления не получено", Toast.LENGTH_LONG).show()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkNotificationPermissionAndStartService()
        setupUI()
        observeViewModel()

        val jsonTrack = intent.getStringExtra(EXTRA_TRACK) ?: return finish()
        currentTrack = Gson().fromJson(jsonTrack, Track::class.java) ?: return finish()

        bottomSheetBehavior = BottomSheetBehavior.from(binding.playlistsBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        viewModel.playlists
            .onEach { playlists ->

                println("DEBUG: Получено плейлистов: ${playlists.size}")
                playlists.forEach {
                    println("DEBUG: Плейлист: ${it.name}, ID: ${it.id}, треков: ${it.trackCount}")
                }

                bottomSheetAdapter = BottomSheetPlaylistsAdapter(
                    playlists = playlists,
                    currentTrack = currentTrack,
                    playlistInteractor = playlistInteractor,
                    bottomSheetBehavior = bottomSheetBehavior
                )
                binding.playlistsRecyclerBottomSheet.adapter = bottomSheetAdapter
                binding.playlistsRecyclerBottomSheet.layoutManager =
                    LinearLayoutManager(this@PlayerActivity)
            }
            .launchIn(lifecycleScope)

        binding.playButton.setOnPlaybackClickListener {
            viewModel.playbackControl()
        }

        binding.toolbarPlayer.setNavigationOnClickListener {
            isFinishingByUser = true
            finish()
        }

        binding.favoritesBtn.setOnClickListener { viewModel.onLikeClicked(currentTrack) }

        binding.addButton.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        binding.createPlaylistButton.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            openNewPlaylistFragment()
        }

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                binding.overlay.visibility =
                    if (newState == BottomSheetBehavior.STATE_HIDDEN) View.GONE else View.VISIBLE
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.overlay.alpha = (0.0f).coerceAtLeast(slideOffset.coerceAtMost(1.0f))
            }
        })

        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount == 0) {
                binding.newPlaylistContainer.visibility = View.GONE
            }
        }

    }

    private fun checkNotificationPermissionAndStartService() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            when {
                androidx.core.content.ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) == android.content.pm.PackageManager.PERMISSION_GRANTED -> {
                    startMusicService()
                }

                shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS) -> {
                    Toast.makeText(
                        this,
                        "Разрешение на уведомления нужно для отображения состояния плеера",
                        Toast.LENGTH_LONG
                    ).show()
                    requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                }

                else -> {
                    requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            startMusicService()
        }
    }

    private fun startMusicService() {
        val serviceIntent = Intent(this, MusicService::class.java)
        startService(serviceIntent)
        bindService(serviceIntent, serviceConnection, BIND_AUTO_CREATE)
    }

    private fun openNewPlaylistFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.newPlaylistContainer, NewPlaylistFragment())
            .addToBackStack(null)
            .commit()
        binding.newPlaylistContainer.visibility = View.VISIBLE
    }

    private fun setupUI() {
        binding.playButton.isEnabled = false
    }

    private fun observeViewModel() {
        viewModel.uiState.observe(this) { uiState: PlayerUiState ->
            when (uiState.playerState) {
                is PlayerState.Prepared -> {
                    binding.playButton.isEnabled = true
                    binding.playButton.isPlaying = false
                }

                is PlayerState.Playing -> {
                    binding.playButton.isEnabled = true
                    binding.playButton.isPlaying = true
                }

                is PlayerState.Paused -> {
                    binding.playButton.isEnabled = true
                    binding.playButton.isPlaying = false
                }

                is PlayerState.Default -> {
                    binding.playButton.isEnabled = false
                    binding.playButton.isPlaying = false
                }
            }

            binding.musicTimeDuration.text = uiState.currentTime
        }
        viewModel.isFavorite.observe(this) { liked -> setFavoriteButton(liked) }
    }

    private fun setFavoriteButton(isFavorite: Boolean) {
        val icon = if (isFavorite) R.drawable.like_button_enable else R.drawable.like_button
        binding.favoritesBtn.setImageResource(icon)
    }

    private fun bindTrackInfo(track: Track) {
        binding.apply {
            musicTrackName.text = track.trackName
            playerGroupName.text = track.artistName
            durationTime.text =
                SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
            albumName.text = track.collectionName ?: "N/A"
            yearName.text = track.releaseDate.substring(0, 4)
            genreName.text = track.primaryGenreName
            countryName.text = track.country

            Glide.with(this@PlayerActivity)
                .load(track.artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg"))
                .placeholder(R.drawable.placeholder)
                .transform(RoundedCorners(16))
                .into(musicTrackCover)
        }
    }


    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicService.MusicServiceBinder
            musicService = binder.getService()
            musicService?.hideNotification()

            musicService?.setPlayerStateListener(object : MusicService.PlayerStateListener {
                override fun onStateChanged(state: PlayerState) {
                    runOnUiThread {
                        viewModel.updatePlayerStateFromService(state)
                    }
                }
            })
            viewModel.attachService(musicService!!)

            viewModel.observeFavorite(currentTrack.trackId)
            currentTrack.previewUrl?.let { viewModel.preparePlayer(it) }
            bindTrackInfo(currentTrack)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            musicService = null
        }
    }

    override fun onStart() {
        super.onStart()
        if (musicService?.isPlaying() == true) {
            musicService?.hideNotification()
        }
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        musicService?.pausePlayer()
        unbindService(serviceConnection)
        musicService = null
    }


    override fun onStop() {
        super.onStop()
        if (isFinishingByUser) {
            musicService?.pausePlayer()
            stopService(Intent(this, MusicService::class.java))
        } else if (musicService?.isPlaying() == true) {
            musicService?.showNotification(currentTrack.trackName, currentTrack.artistName)
        }
    }

}
