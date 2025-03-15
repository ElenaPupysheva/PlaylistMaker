package com.practicum.playlistmaker.player.domain.api

interface AudioRepository {
    fun preparePlayer(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit)
    fun startPlayer()
    fun pausePlayer()
    fun playbackControl()
    fun releasePlayer()
    fun isPlaying(): Boolean
    fun getCurrentPositionMs(): Int
}