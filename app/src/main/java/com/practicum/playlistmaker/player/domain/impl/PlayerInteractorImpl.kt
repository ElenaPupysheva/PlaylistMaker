package com.practicum.playlistmaker.player.domain.impl

import com.practicum.playlistmaker.player.data.dto.PlayerState
import com.practicum.playlistmaker.player.domain.api.AudioRepository
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import kotlinx.coroutines.flow.StateFlow

class PlayerInteractorImpl(
    private val audioPlayer: AudioRepository
) : PlayerInteractor {

    override val playerStateFlow: StateFlow<PlayerState> = audioPlayer.playerStateFlow

    override fun preparePlayer(url: String) {
        audioPlayer.preparePlayer(url)
    }

    override fun startPlayer() {
        audioPlayer.startPlayer()
    }

    override fun pausePlayer() {
        audioPlayer.pausePlayer()
    }

    override fun playbackControl() {
        audioPlayer.playbackControl()
    }

    override fun releasePlayer() {
        audioPlayer.releasePlayer()
    }

    override fun isPlaying(): Boolean {
        return audioPlayer.isPlaying()
    }

    override fun getCurrentPositionMs(): Int {
        return audioPlayer.getCurrentPositionMs()
    }
}
