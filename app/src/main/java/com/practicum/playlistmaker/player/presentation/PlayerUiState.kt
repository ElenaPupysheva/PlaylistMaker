package com.practicum.playlistmaker.player.presentation

import com.practicum.playlistmaker.player.data.dto.PlayerStates

data class PlayerUiState(
    val playerState: PlayerStates,
    val currentTime: String
)