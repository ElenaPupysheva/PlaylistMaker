package com.practicum.playlistmaker.player.data.dto

sealed class PlayerState {
    object Default : PlayerState()
    object Prepared : PlayerState()
    object Playing : PlayerState()
    object Paused : PlayerState()
}
