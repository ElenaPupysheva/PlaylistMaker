package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.models.Track

interface SearchInteractor {
    fun searchTracks(expression: String): List<Track>
}