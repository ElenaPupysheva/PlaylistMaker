package com.practicum.playlistmaker.player.ui

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityPlayerBinding
import com.practicum.playlistmaker.domain.models.EXTRA_TRACK
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.player.data.dto.PlayerStates
import com.practicum.playlistmaker.player.presentation.PlayerUiState
import com.practicum.playlistmaker.player.presentation.PlayerViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

import java.util.Locale

class PlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerBinding

    private val viewModel: PlayerViewModel by viewModel()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        observeViewModel()

        val jsonTrack = intent.getStringExtra(EXTRA_TRACK) ?: return finish()
        val track = Gson().fromJson(jsonTrack, Track::class.java) ?: return finish()
        val url = track.previewUrl ?: return finish()

        viewModel.preparePlayer(url)
        bindTrackInfo(track)

        binding.playButton.setOnClickListener {
            viewModel.playbackControl()
        }

        binding.toolbarPlayer.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupUI() {
        binding.playButton.isEnabled = false
    }

    private fun observeViewModel() {
        viewModel.uiState.observe(this) { uiState: PlayerUiState ->
            when (uiState.playerState) {
                PlayerStates.PREPARED -> {
                    binding.playButton.isEnabled = true
                    binding.playButton.setImageResource(R.drawable.play_button)
                }
                PlayerStates.PLAYING -> {
                    binding.playButton.isEnabled = true
                    binding.playButton.setImageResource(R.drawable.pause_button)
                }
                PlayerStates.PAUSED -> {
                    binding.playButton.isEnabled = true
                    binding.playButton.setImageResource(R.drawable.play_button)
                }
                PlayerStates.DEFAULT -> {
                    binding.playButton.isEnabled = false
                    binding.playButton.setImageResource(R.drawable.play_button)
                }
            }

            binding.musicTimeDuration.text = uiState.currentTime
        }
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

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }
}
