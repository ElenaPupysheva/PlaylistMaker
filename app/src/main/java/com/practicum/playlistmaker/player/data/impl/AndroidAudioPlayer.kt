package com.practicum.playlistmaker.player.data.impl

import android.media.MediaPlayer
import com.practicum.playlistmaker.player.data.dto.PlayerState
import com.practicum.playlistmaker.player.domain.api.AudioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AndroidAudioPlayer(private val mediaPlayer: MediaPlayer) : AudioRepository {
    private val _playerStateFlow = MutableStateFlow<PlayerState>(PlayerState.Default())
    override val playerStateFlow: StateFlow<PlayerState> = _playerStateFlow

    override fun preparePlayer(url: String) {
        releasePlayer()
        mediaPlayer.apply {
            setDataSource(url)
            prepareAsync()
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
