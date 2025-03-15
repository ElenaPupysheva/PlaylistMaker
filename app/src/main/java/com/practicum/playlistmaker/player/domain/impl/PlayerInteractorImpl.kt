package com.practicum.playlistmaker.player.domain.impl

import com.practicum.playlistmaker.player.domain.api.AudioRepository

class PlayerInteractorImpl(
    private val audioPlayer: AudioRepository
) {

    fun preparePlayer(
        url: String,
        onPrepared: () -> Unit,
        onCompletion: () -> Unit
    ) {
        audioPlayer.preparePlayer(url, onPrepared, onCompletion)
    }

    fun startPlayer() {
        audioPlayer.startPlayer()
    }

    fun pausePlayer() {
        audioPlayer.pausePlayer()
    }

    fun playbackControl() {
        audioPlayer.playbackControl()
    }

    fun releasePlayer() {
        audioPlayer.releasePlayer()
    }

    fun getCurrentPositionMs(): Int {
        return audioPlayer.getCurrentPositionMs()
    }
}
