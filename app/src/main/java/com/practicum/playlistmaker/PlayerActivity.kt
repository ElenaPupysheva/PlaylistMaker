package com.practicum.playlistmaker

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.Track
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

        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val jsonTrack = intent.getStringExtra("track")
        val track = Gson().fromJson(jsonTrack, Track::class.java)
        if (jsonTrack == null) {
            finish()
            return
        }

        trackName.text = track.trackName
        groupName.text = track.artistName
        timeDuration.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
        durationText.text = timeDuration.text

        if (track.collectionName.isNullOrEmpty()) {
            albumName.visibility = View.GONE
        } else {
            albumName.text = track.collectionName
        }

        yearName.text = track.releaseDate.substring(0, 4)
        genreName.text = track.primaryGenreName
        countryName.text = track.country

        Glide.with(this)
            .load(track.artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg"))
            .placeholder(R.drawable.placeholder)
            .transform(RoundedCorners(16))
            .into(trackCover)
    }
}
