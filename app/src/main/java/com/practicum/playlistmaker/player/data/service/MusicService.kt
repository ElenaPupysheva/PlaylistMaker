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

internal class MusicService : Service() {
    private val binder = MusicServiceBinder()
    private var mediaPlayer: MediaPlayer? = null

    private var playerState: PlayerState = PlayerState.Default()
    private var playerStateListener: PlayerStateListener? = null

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

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

    inner class MusicServiceBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }

    fun setPlayerStateListener(listener: PlayerStateListener) {
        playerStateListener = listener
    }

    fun showNotification(title: String, artist: String) {
        val channel =
            NotificationChannel(CHANNEL_ID, "Playback", NotificationManager.IMPORTANCE_LOW)
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)


        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(artist)
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
        stopForeground(false)
    }

    fun preparePlayer(url: String) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(url)
            setOnPreparedListener {
                playerState = PlayerState.Prepared()
                playerStateListener?.onStateChanged(playerState)
            }
            setOnCompletionListener {
                playerState = PlayerState.Prepared()
                playerStateListener?.onStateChanged(playerState)
                stopForeground(true)
            }
            prepareAsync()
        }
    }

    fun getCurrentPlayerPosition(): Int {
        return mediaPlayer?.currentPosition ?: 0
    }

    fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying == true
    }

    fun startPlayer() {
        mediaPlayer?.start()
        playerState = PlayerState.Playing()
        playerStateListener?.onStateChanged(playerState)
    }

    fun pausePlayer() {
        mediaPlayer?.pause()
        playerState = PlayerState.Paused()
        playerStateListener?.onStateChanged(playerState)
    }

    interface PlayerStateListener {
        fun onStateChanged(state: PlayerState)
    }

    companion object {
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "music_playback_channel"
    }

}