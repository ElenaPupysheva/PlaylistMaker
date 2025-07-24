package com.practicum.playlistmaker.player.domain.api

import com.practicum.playlistmaker.player.data.dto.PlayerState
import kotlinx.coroutines.flow.StateFlow

interface AudioRepository {
    fun preparePlayer(url: String)
    val playerStateFlow: StateFlow<PlayerState>
    fun startPlayer()
    fun pausePlayer()
    fun playbackControl()
    fun releasePlayer()
    fun isPlaying(): Boolean
    fun getCurrentPositionMs(): Int
}