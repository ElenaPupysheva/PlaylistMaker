package com.practicum.playlistmaker.player.presentation
import android.media.MediaPlayer
import com.practicum.playlistmaker.domain.api.AudioPlayer
import com.practicum.playlistmaker.domain.models.STATE_DEFAULT
import com.practicum.playlistmaker.domain.models.STATE_PAUSED
import com.practicum.playlistmaker.domain.models.STATE_PLAYING
import com.practicum.playlistmaker.domain.models.STATE_PREPARED
class AndroidAudioPlayer: AudioPlayer {
    private var mediaPlayer: MediaPlayer? = null
    private var currentState = STATE_DEFAULT

    private var trackUrl: String? = null

    private var onPreparedCallback: (() -> Unit)? = null
    private var onCompletionCallback: (() -> Unit)? = null

    override fun preparePlayer(
        url: String,
        onPrepared: () -> Unit,
        onCompletion: () -> Unit
    ) {
        releasePlayer()
        trackUrl = url
        onPreparedCallback = onPrepared
        onCompletionCallback = onCompletion

        mediaPlayer = MediaPlayer().apply {
            setDataSource(url)
            prepareAsync()
            setOnPreparedListener {
                currentState = STATE_PREPARED
                onPreparedCallback?.invoke()
            }
            setOnCompletionListener {
                currentState = STATE_PREPARED
                onCompletionCallback?.invoke()
            }
        }
    }

    override fun startPlayer() {
        if (currentState == STATE_PREPARED || currentState == STATE_PAUSED) {
            mediaPlayer?.start()
            currentState = STATE_PLAYING
        }
    }

    override fun pausePlayer() {
        if (currentState == STATE_PLAYING) {
            mediaPlayer?.pause()
            currentState = STATE_PAUSED
        }
    }

    override fun playbackControl() {
        when(currentState) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    override fun releasePlayer() {
        mediaPlayer?.release()
        mediaPlayer = null
        currentState = STATE_DEFAULT
    }

    override fun getCurrentPositionMs(): Int {
        return mediaPlayer?.currentPosition ?: 0
    }

    override fun getPlayerState(): Int {
        return currentState
    }
}
