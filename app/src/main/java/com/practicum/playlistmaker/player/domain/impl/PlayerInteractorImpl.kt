package com.practicum.playlistmaker.player.domain.impl

import com.practicum.playlistmaker.player.domain.api.AudioRepository
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor

class PlayerInteractorImpl(
    private val audioPlayer: AudioRepository
) : PlayerInteractor {

    override fun preparePlayer(
        url: String,
        onPrepared: () -> Unit,
        onCompletion: () -> Unit
    ) {
        audioPlayer.preparePlayer(url, onPrepared, onCompletion)
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
