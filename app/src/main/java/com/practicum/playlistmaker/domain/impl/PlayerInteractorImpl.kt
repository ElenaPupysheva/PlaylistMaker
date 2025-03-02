package com.practicum.playlistmaker.domain.impl

import com.practicum.playlistmaker.domain.api.AudioPlayer
import com.practicum.playlistmaker.domain.api.PlayerInteractor

class PlayerInteractorImpl(
    private val audioPlayer: AudioPlayer
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

    override fun getPlayerState(): Int {
        return audioPlayer.getPlayerState()
    }

    override fun getCurrentPositionMs(): Int {
        return audioPlayer.getCurrentPositionMs()
    }
}
