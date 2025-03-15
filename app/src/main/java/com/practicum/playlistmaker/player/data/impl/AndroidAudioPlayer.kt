package com.practicum.playlistmaker.player.data.impl

import android.media.MediaPlayer
import com.practicum.playlistmaker.player.domain.api.AudioRepository

class AndroidAudioPlayer(private val mediaPlayer: MediaPlayer) : AudioRepository {
    private var onPreparedCallback: (() -> Unit)? = null
    private var onCompletionCallback: (() -> Unit)? = null

    override fun preparePlayer(
        url: String,
        onPrepared: () -> Unit,
        onCompletion: () -> Unit
    ) {
        releasePlayer()
        onPreparedCallback = onPrepared
        onCompletionCallback = onCompletion

        mediaPlayer.apply {
            releasePlayer()
            setDataSource(url)
            prepareAsync()
            setOnPreparedListener { onPreparedCallback?.invoke() }
            setOnCompletionListener { onCompletionCallback?.invoke() }
        }
    }

    override fun startPlayer() {
        mediaPlayer.start()
    }

    override fun pausePlayer() {
        mediaPlayer.pause()
    }

    override fun playbackControl() {
        mediaPlayer.let {
            if (it.isPlaying) pausePlayer() else startPlayer()
        }
    }

    override fun releasePlayer() {
        mediaPlayer.reset()
    }

    override fun isPlaying(): Boolean {
        return mediaPlayer.isPlaying
    }

    override fun getCurrentPositionMs(): Int {
        return mediaPlayer.currentPosition
    }
}
