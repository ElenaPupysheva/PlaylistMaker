package com.practicum.playlistmaker.player.presentation

import com.practicum.playlistmaker.player.data.dto.PlayerState

data class PlayerUiState(
    val playerState: PlayerState,
    val currentTime: String
)