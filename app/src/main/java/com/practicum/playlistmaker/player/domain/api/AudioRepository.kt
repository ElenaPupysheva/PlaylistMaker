package com.practicum.playlistmaker.player.domain.api

import com.practicum.playlistmaker.player.data.dto.PlayerState
import kotlinx.coroutines.flow.StateFlow

interface AudioRepository {
    val playerStateFlow: StateFlow<PlayerState>
    fun preparePlayer(url: String)
    fun startPlayer()
    fun pausePlayer()
    fun playbackControl()
    fun releasePlayer()
    fun isPlaying(): Boolean
    fun getCurrentPositionMs(): Int
}