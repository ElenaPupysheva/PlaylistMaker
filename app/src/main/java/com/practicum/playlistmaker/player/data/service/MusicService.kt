package com.practicum.playlistmaker.player.data.service

import android.app.NotificationChannel
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.player.data.dto.PlayerState
import android.app.NotificationManager
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
import android.os.Build
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class MusicService : Service() {
    private val binder = MusicServiceBinder()

    private val _playerStateFlow = MutableStateFlow<PlayerState>(PlayerState.Default())
    val playerStateFlow = _playerStateFlow.asStateFlow()
    private var mediaPlayer: MediaPlayer? = null
    private var isPrepared = false
    private var trackName: String? = null
    private var artistName: String? = null

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Playback",
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    override fun onBind(intent: Intent?): IBinder {
        trackName = intent?.getStringExtra(EXTRA_TRACK_NAME)
        artistName = intent?.getStringExtra(EXTRA_ARTIST_NAME)
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Playlist Maker")
            .setContentText("Подготовка к воспроизведению")
            .setSmallIcon(R.drawable.play_button)
            .build()

        ServiceCompat.startForeground(
            this,
            NOTIFICATION_ID,
            notification,
            FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
        )

        return START_NOT_STICKY
    }

    inner class MusicServiceBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }

    fun showNotification(title: String, artist: String) {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Playlist Maker")
            .setContentText("$artist - $title")
            .setSmallIcon(R.drawable.play_button)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        ServiceCompat.startForeground(
            this,
            NOTIFICATION_ID,
            notification,
            FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
        )
    }

    fun hideNotification() {
        stopForeground(true)
    }

    fun preparePlayer(url: String) {
        if (mediaPlayer != null || isPrepared) {
            return
        }
        mediaPlayer = MediaPlayer().apply {
            setDataSource(url)
            setOnPreparedListener {
                _playerStateFlow.value = PlayerState.Prepared()
                isPrepared = true
            }
            setOnCompletionListener {
                seekTo(0)
                _playerStateFlow.value = PlayerState.Complete()
                isPrepared = false
                stopPlayerAndService()
            }

            prepareAsync()
        }
    }

    fun startPlayer() {
        mediaPlayer?.start()
        _playerStateFlow.value = PlayerState.Playing()
    }

    fun pausePlayer() {
        mediaPlayer?.pause()
        _playerStateFlow.value = PlayerState.Paused()
    }

    fun playbackControl() {
        if (mediaPlayer?.isPlaying == true) pausePlayer() else startPlayer()
    }

    fun getCurrentPlayerPosition(): Int = mediaPlayer?.currentPosition ?: 0

    fun isPlaying(): Boolean = mediaPlayer?.isPlaying == true

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        stopPlayerAndService()
    }

    fun stopPlayerAndService() {
        mediaPlayer?.apply {
            stop()
            release()
        }
        mediaPlayer = null
        isPrepared = false
        stopForeground(true)
        stopSelf()
    }

    companion object {
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "music_playback_channel"
        const val EXTRA_TRACK_URL = "extra_track_url"
        const val EXTRA_TRACK_NAME = "extra_track_name"
        const val EXTRA_ARTIST_NAME = "extra_artist_name"
    }
}
