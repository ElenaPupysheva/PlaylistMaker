package com.practicum.playlistmaker.player.data.dto

sealed class PlayerState {
    class Default : PlayerState()
    class Prepared : PlayerState()
    class Playing : PlayerState()
    class Paused : PlayerState()
}
