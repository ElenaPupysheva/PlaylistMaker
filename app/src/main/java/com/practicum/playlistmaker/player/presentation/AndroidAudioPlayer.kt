package com.practicum.playlistmaker.player.presentation

import android.media.MediaPlayer
import com.practicum.playlistmaker.player.domain.api.AudioPlayer

class AndroidAudioPlayer : AudioPlayer {
    private var mediaPlayer: MediaPlayer? = null
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

        mediaPlayer = MediaPlayer().apply {
            setDataSource(url)
            prepareAsync()
            setOnPreparedListener { onPreparedCallback?.invoke() }
            setOnCompletionListener { onCompletionCallback?.invoke() }
        }
    }

    override fun startPlayer() {
        mediaPlayer?.start()
    }

    override fun pausePlayer() {
        mediaPlayer?.pause()
    }

    override fun playbackControl() {
        mediaPlayer?.let {
            if (it.isPlaying) pausePlayer() else startPlayer()
        }
    }

    override fun releasePlayer() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun getCurrentPositionMs(): Int {
        return mediaPlayer?.currentPosition ?: 0
    }
}
