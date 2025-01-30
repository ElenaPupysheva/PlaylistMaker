package com.practicum.playlistmaker.ui.player

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.Group
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import com.practicum.playlistmaker.EXTRA_TRACK
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.STATE_DEFAULT
import com.practicum.playlistmaker.STATE_PAUSED
import com.practicum.playlistmaker.STATE_PLAYING
import com.practicum.playlistmaker.STATE_PREPARED
import com.practicum.playlistmaker.TIMER_UPDATE_TIME
import com.practicum.playlistmaker.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var trackCover: ImageView
    private lateinit var trackName: TextView
    private lateinit var groupName: TextView
    private lateinit var timeDuration: TextView
    private lateinit var durationText: TextView
    private lateinit var albumName: TextView
    private lateinit var yearName: TextView
    private lateinit var genreName: TextView
    private lateinit var countryName: TextView
    private lateinit var group: Group
    private lateinit var playButton: ImageButton
    private lateinit var url: String
    private var mediaPlayer = MediaPlayer()
    private var playerState = STATE_DEFAULT

    private val handler = Handler(Looper.getMainLooper())


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        toolbar = findViewById(R.id.toolbar_player)
        trackCover = findViewById(R.id.musicTrackCover)
        trackName = findViewById(R.id.musicTrackName)
        groupName = findViewById(R.id.playerGroupName)
        timeDuration = findViewById(R.id.musicTimeDuration)
        durationText = findViewById(R.id.durationTime)
        albumName = findViewById(R.id.albumName)
        yearName = findViewById(R.id.yearName)
        genreName = findViewById(R.id.genreName)
        countryName = findViewById(R.id.countryName)
        group = findViewById(R.id.group)
        playButton = findViewById(R.id.playButton)

        val jsonTrack = intent.getStringExtra(EXTRA_TRACK)
        if (jsonTrack == null) {
            finish()
            return
        }
        val track = Gson().fromJson(jsonTrack, Track::class.java)

        if (track.previewUrl.isNullOrEmpty()) {
            finish()
            return
        }
        url = track.previewUrl

        trackName.text = track.trackName
        groupName.text = track.artistName
        durationText.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
        if (!track.collectionName.isNullOrEmpty()) {
            albumName.text = track.collectionName
            group.visibility = View.VISIBLE
        } else {
            group.visibility = View.GONE
        }

        yearName.text = track.releaseDate.substring(0, 4)
        genreName.text = track.primaryGenreName
        countryName.text = track.country

        Glide.with(this)
            .load(track.artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg"))
            .placeholder(R.drawable.placeholder)
            .transform(RoundedCorners(16))
            .into(trackCover)

        preparePlayer()
        playButton.setOnClickListener {
            playbackControl()
        }

        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private val startProgressRunnable = object : Runnable {
        override fun run() {
            if (playerState == STATE_PLAYING) {
                timeDuration.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
                handler.postDelayed(this, TIMER_UPDATE_TIME)
            }
        }
    }
    private fun preparePlayer() {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playButton.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playButton.setImageResource(R.drawable.play_button)
            timeDuration.text = "0:00"
            playerState = STATE_PREPARED
            handler.removeCallbacks(startProgressRunnable)
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playButton.setImageResource(R.drawable.pause_button)
        playerState = STATE_PLAYING
        handler.post(startProgressRunnable)
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playButton.setImageResource(R.drawable.play_button)
        playerState = STATE_PAUSED
        handler.removeCallbacks(startProgressRunnable)
    }

    private fun playbackControl() {
        when(playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (playerState == STATE_PLAYING) {
            pausePlayer()
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        handler.removeCallbacks(startProgressRunnable)
    }

}
