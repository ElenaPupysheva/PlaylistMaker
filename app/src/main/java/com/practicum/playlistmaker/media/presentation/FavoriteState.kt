package com.practicum.playlistmaker.media.presentation

import com.practicum.playlistmaker.domain.models.Track

sealed interface FavoriteState {
        object Loading : FavoriteState
        object Empty : FavoriteState
        data class Content(val tracks: List<Track>) : FavoriteState

}