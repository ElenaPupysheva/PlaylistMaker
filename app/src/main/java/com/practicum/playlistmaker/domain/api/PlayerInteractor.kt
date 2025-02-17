package com.practicum.playlistmaker.domain.api

interface PlayerInteractor {
    fun preparePlayer(
        url: String,
        onPrepared: () -> Unit,
        onCompletion: () -> Unit
    )
    fun startPlayer()
    fun pausePlayer()
    fun playbackControl()

    fun releasePlayer()
    fun getPlayerState(): Int
    fun getCurrentPositionMs(): Int
}