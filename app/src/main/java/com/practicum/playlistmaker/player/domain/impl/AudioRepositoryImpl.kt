package com.practicum.playlistmaker.player.domain.impl

import com.practicum.playlistmaker.player.data.dto.PlayerState
import com.practicum.playlistmaker.player.data.service.MusicService
import com.practicum.playlistmaker.player.domain.api.AudioRepository
import kotlinx.coroutines.flow.StateFlow

internal class AudioRepositoryImpl(
    private val musicService: MusicService
) : AudioRepository {

    override val playerStateFlow: StateFlow<PlayerState>
        get() = musicService.playerStateFlow

    override fun preparePlayer(url: String) {
        musicService.preparePlayer(url)
    }

    override fun startPlayer() {
        musicService.startPlayer()
    }

    override fun pausePlayer() {
        musicService.pausePlayer()
    }

    override fun playbackControl() {
        musicService.playbackControl()
    }

    override fun releasePlayer() {
        musicService.stopPlayerAndService()
    }

    override fun isPlaying(): Boolean {
        return musicService.isPlaying()
    }

    override fun getCurrentPositionMs(): Int {
        return musicService.getCurrentPlayerPosition()
    }
}
