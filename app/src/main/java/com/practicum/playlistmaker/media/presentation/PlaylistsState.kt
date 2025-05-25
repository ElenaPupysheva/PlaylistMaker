package com.practicum.playlistmaker.media.presentation

import com.practicum.playlistmaker.domain.models.Playlist

sealed class PlaylistsState {
    object Loading : PlaylistsState()
    object Empty : PlaylistsState()
    data class Content(val playlists: List<Playlist>) : PlaylistsState()
}
