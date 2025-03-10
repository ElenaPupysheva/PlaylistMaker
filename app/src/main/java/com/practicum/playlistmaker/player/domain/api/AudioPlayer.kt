package com.practicum.playlistmaker.player.domain.api

interface AudioPlayer {
        fun preparePlayer(
            url: String,
            onPrepared: () -> Unit,
            onCompletion: () -> Unit
        )
        fun startPlayer()
        fun pausePlayer()
        fun playbackControl()

        fun releasePlayer()
        fun getCurrentPositionMs(): Int

}